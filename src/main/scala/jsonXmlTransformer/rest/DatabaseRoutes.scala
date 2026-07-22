package jsonXmlTransformer.rest

import jsonXmlTransformer.model.units.User
import jsonXmlTransformer.service.convert.XmlMapperService
import jsonXmlTransformer.service.database.DatabaseService
import zio._
import zio.http._
import zio.json._


case class DatabaseRoutes(dbService: DatabaseService, xmlService: XmlMapperService) {
  val routes: Routes[Any, Response] = Routes(

    Method.POST / "create" -> handler { (req: Request) =>
      for {
        bodyStr <- req.body.asString.orDie
        userDefault = bodyStr.fromJson[User]
        response <- userDefault match {
          case Left(err) => ZIO.succeed {
            val errXml = xmlService.errorXml(s"Invalid JSON: $err")
            Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
          }
          case Right(user) =>
            (for {
              _ <- dbService.saveUser(user)
              successXml = "<response>\n<status>success</status>\n<message>User successfully created</message>\n</response>"
            } yield Response(status = Status.Created, body = Body.fromString(successXml).contentType(MediaType.application.xml)))
              .catchAll { err =>
                ZIO.succeed {
                  val errXml = xmlService.errorXml(s"Server error: ${err.getMessage}")
                  Response(status = Status.InternalServerError, body = Body.fromString(errXml).contentType(MediaType.application.xml))
                }
              }
        }
      } yield response
    },

    Method.GET / "read" -> handler { (req: Request) =>
      val nameOpt = req.queryParam("name").filter(_.nonEmpty)
      val ageOpt = req.queryParam("age").flatMap(_.toIntOption)
      val actualWorkOpt = req.queryParam("actual_work").filter(_.nonEmpty)

      val result = (nameOpt, ageOpt, actualWorkOpt) match {
        case (Some(name), Some(age), Some(actualWork)) =>
          dbService.findUser(name, age, actualWork).flatMap {
            case Some(user) => xmlService.toXml(user).map { xml =>
              Response(status = Status.Ok, body = Body.fromString(xml).contentType(MediaType.application.xml))
            }
            case None => ZIO.succeed {
              val errXml = xmlService.errorXml("User not found")
              Response(status = Status.NotFound, body = Body.fromString(errXml).contentType(MediaType.application.xml))
            }
          }
        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required parameters: name, age, or actual_work")
          Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
        }
      }
      result.catchAll { err =>
        ZIO.succeed {
          val errXml = xmlService.errorXml(s"Internal error: ${err.getMessage}")
          Response(status = Status.InternalServerError, body = Body.fromString(errXml).contentType(MediaType.application.xml))
        }
      }
    }
  )
}