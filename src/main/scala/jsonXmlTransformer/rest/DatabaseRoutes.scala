package jsonXmlTransformer.rest

import jsonXmlTransformer.model.units.{User, UserRow}
import jsonXmlTransformer.service.convert.XmlMapperService
import jsonXmlTransformer.service.database.DatabaseService
import zio._
import zio.http.Status._
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
            Response(BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
          }
          case Right(user) =>
            val response = for {
              _ <- dbService.saveUser(user)
              sucXml = xmlService.successXml("User successfully created.")
            } yield Response(Created, body = Body.fromString(sucXml).contentType(xmlContentType))

            response.catchAll { err =>
              ZIO.succeed {
                val errXml = xmlService.errorXml(s"Server error: ${err.getMessage}")
                Response(InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
              }
            }
        }
      } yield response
    },


    Method.GET / "read" -> handler { (req: Request) =>
      val nameOpt = req.queryParam("name").filter(_.nonEmpty)
      val ageOpt = req.queryParam("age").flatMap(_.toIntOption)
      val actualWorkOpt = req.queryParam("actual_work").filter(_.nonEmpty)

      val read = (nameOpt, ageOpt, actualWorkOpt) match {
        case (Some(name), Some(age), Some(actualWork)) =>
          dbService.findUser(name, age, actualWork).flatMap {
            case Some(user) => xmlService.toXml(user).map { xml =>
              Response(Ok, body = Body.fromString(xml).contentType(xmlContentType))
            }
            case None => ZIO.succeed {
              val errXml = xmlService.errorXml("User not found.")
              Response(NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }
        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required parameters: name, age, or actual_work.")
          Response(BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
      read.catchAll { err =>
        ZIO.succeed {
          val errXml = xmlService.errorXml(s"Internal error: ${err.getMessage}")
          Response(InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
    },


    Method.PUT / "update" -> handler { (req: Request) =>
      val nameOpt = req.queryParam("name").filter(_.nonEmpty)
      val ageOpt = req.queryParam("age").flatMap(_.toIntOption)
      val workOpt = req.queryParam("actual_work").filter(_.nonEmpty)

      for {
        bodyStr <- req.body.asString.orDie
        userDefault = bodyStr.fromJson[User]
        response <- userDefault match {
          case Left(err) => ZIO.succeed {
            val errXml = xmlService.errorXml(s"Invalid JSON: $err")
            Response(BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
          }
          case Right(user) => (nameOpt, ageOpt, workOpt) match {
            case (Some(name), Some(age), Some(work)) =>
              val newUser = UserRow(
                None,
                user.name,
                user.age,
                user.actualWork,
                if (user.previousWorks != null) user.previousWorks.mkString(", ") else "",
                user.currentStatusActive
              )

              val update = for {
                affected <- dbService.updateUser(name, age, work, newUser)
                response = if (affected > 0 ) {
                  val sucXml = xmlService.successXml("User successfully updated.")
                  Response(Ok, body = Body.fromString(sucXml).contentType(xmlContentType))
                } else {
                  val errXml = xmlService.errorXml("User not found.")
                  Response(NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
                }
              } yield response

              update.catchAll { err => ZIO.succeed {
                val errXml = xmlService.errorXml(s"Server error: ${err.getMessage}")
                Response(InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
                }
              }

            case _ => ZIO.succeed {
              val errXml = xmlService.errorXml("Missing required parameters: name, age or actual_work.")
              Response(BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }
        }
      } yield response
    },


    Method.DELETE / "delete" -> handler { (req: Request) =>
      val nameOpt = req.queryParam("name").filter(_.nonEmpty)
      val ageOpt  = req.queryParam("age").flatMap(_.toIntOption)
      val workOpt = req.queryParam("actual_work").filter(_.nonEmpty)

      (nameOpt, ageOpt, workOpt) match {
        case (Some(name), Some(age), Some(work)) =>
          dbService.deleteUser(name, age, work).map {
            case 0 =>
              val errXml = xmlService.errorXml("User not found.")
              Response(NotFound, body = Body.fromString(errXml).contentType(xmlContentType))
            case _ =>
              val sucXml = xmlService.successXml("User deleted.")
              Response(Ok, body = Body.fromString(sucXml).contentType(xmlContentType))
          }.catchAll { err =>
            ZIO.succeed{
              val errXml = xmlService.errorXml(err.getMessage)
              Response(InternalServerError, body = Body.fromString(errXml).contentType(xmlContentType))
            }
          }

        case _ => ZIO.succeed {
          val errXml = xmlService.errorXml("Missing required query parameters: name, age or actual_work.")
          Response(BadRequest, body = Body.fromString(errXml).contentType(xmlContentType))
        }
      }
    }
  )

  private val xmlContentType: MediaType = MediaType.application.xml
}