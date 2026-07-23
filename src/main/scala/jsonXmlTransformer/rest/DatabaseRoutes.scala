package jsonXmlTransformer.rest

import jsonXmlTransformer.model.units.{User, UserRow}
import jsonXmlTransformer.service.convert.XmlMapperService
import jsonXmlTransformer.service.database.DatabaseService
import zio._
import zio.http.Status.NotFound
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
            Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
          }
          case Right(user) =>
            (for {
              _ <- dbService.saveUser(user)
              successXml = xmlService.successXml("User successfully created.")
            } yield Response(status = Status.Created, body = Body.fromString(successXml).contentType(xmlContentType)))
              .catchAll { err =>
                ZIO.succeed {
                  val errXml = xmlService.errorXml(s"Server error: ${err.getMessage}")
                  Response(status = Status.InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
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
              Response(status = Status.Ok, body = Body.fromString(xml).contentType(xmlContentType))
            }
            case None => ZIO.succeed {
              val errXml = xmlService.errorXml("User not found.")
              Response(status = Status.NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }
        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required parameters: name, age, or actual_work.")
          Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
      result.catchAll { err =>
        ZIO.succeed {
          val errXml = xmlService.errorXml(s"Internal error: ${err.getMessage}")
          Response(status = Status.InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
    },


    Method.PUT / "update" -> handler { (req: Request) =>
      val params = req.url.queryParams
      val oldNameOpt = params.getAll("name").headOption
      val oldAgeOpt = params.getAll("age").headOption.flatMap(_.toIntOption)
      val oldWorkOpt = params.getAll("actual_work").headOption

      (oldNameOpt, oldAgeOpt, oldWorkOpt) match {
        case (Some(name), Some(age), Some(work)) =>
          val processTask = for {
            bodyStr <- req.body.asString
            user <- ZIO.fromEither(bodyStr.fromJson[User]).mapError(_ => "Invalid JSON.")
            userRow = UserRow(
              None,
              user.name,
              user.age, user.actualWork,
              if (user.previousWorks != null) user.previousWorks.mkString(", ") else "",
              user.currentStatusActive)
            affected <- dbService.updateUser(name, age, work, userRow).mapError(_.getMessage)
          } yield affected

          processTask.map {
            case 0 =>
              val errXml = xmlService.errorXml("User not found.")
              Response(status = Status.NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
            case _ =>
              val sucXml = xmlService.successXml("User updated.")
              Response(status = Status.Ok, body = Body.fromString(sucXml).contentType(xmlContentType))
          }.catchAll {
            case "Invalid JSON." => ZIO.succeed {
              val errXml = xmlService.errorXml("Invalid JSON body")
              Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
            }
            case err => ZIO.succeed {
              val errXml = xmlService.errorXml(s"$err")
              Response(status = Status.InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }

        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required query parameters: name, age or actual_work")
          Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
    },


    Method.DELETE / "delete" -> handler { (req: Request) =>
      val params  = req.url.queryParams
      val nameOpt = params.getAll("name").headOption
      val ageOpt  = params.getAll("age").headOption.flatMap(_.toIntOption)
      val workOpt = params.getAll("actual_work").headOption

      (nameOpt, ageOpt, workOpt) match {
        case (Some(name), Some(age), Some(work)) =>
          dbService.deleteUser(name, age, work). map {
            case 0 =>
              val errXml = xmlService.errorXml("User not found.")
              Response(NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
            case _ =>
              val sucXml = xmlService.successXml("User deleted.")
              Response(Status.Ok, body = Body.fromString(sucXml).contentType(xmlContentType))
          }.catchAll { err =>
            ZIO.succeed{
              val errXml = xmlService.errorXml(err.getMessage)
              Response(Status.InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }

        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required query parameters: name, age or actual_work")
          Response(Status.BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
    }
  )

  private val xmlContentType: MediaType = MediaType.application.xml
}