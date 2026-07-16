package transformer.routes

import transformer.convert.XmlMapperService
import transformer.db.DatabaseService
import transformer.user.User
import zio.http._
import zio.json._
import zio._

class HttpServiceLive(dbService: DatabaseService, xmlService: XmlMapperService) extends HttpService {

  override def routes: Routes[Any, Response] = Routes(

    Method.POST / "convert" -> handler { (req: Request) =>
      for {
        bodyString <- req.body.asString.orDie
        userDefault = bodyString.fromJson[User]
        response <- userResult match {
          case Left(err) =>
            xmlService.errorXml(s"Invalid JSON: $err").map { errXml =>
              Response.text(errXml).withStatus(Status.BadRequest).contentType(MediaType.application.xml)
            }
          case Right(user) =>
            if (user.name.equalsIgnoreCase("Виталий")) {
              xmlService.errorXml("This user does not exist.").map { errXml =>
                Response.text(errXml).withStatus(Status.NotFound).contentType(MediaType.application.xml)
              }
            } else {
              xmlService.toXml(user).map { xml =>
                Response.text(xml).withStatus(Status.Ok).contentType(MediaType.application.xml)
              }
            }
        }
      } yield response
    },

    Method.POST / "create" -> handler { (req: Request) =>
      for {
        bodyStr <- req.body.asString
        response <- bodyStr.fromJson[User] match {
          case Left(err) => xmlService.errorXml(s"Invalid JSON: $err").map { errXml =>
            Response.text(errXml).withStatus(Status.BadRequest).contentType(MediaType.application.xml)
          }
          case Right(user) =>
            (for {
              _ <- dbService.saveUser(user)
              successXml = "<response>\n<status>success</status>\n<message>User successfully created</message>\n</response>"
            } yield Response.text(successXml).withStatus(Status.Created).contentType(MediaType.application.xml))
            .catchAll { err =>
              xmlService.errorXml(s"Server error: ${err.getMessage}").map { errXml =>
                Response.text(errXml).withStatus(Status.InternalServerError).contentType(MediaType.application.xml)
              }
            }
        }
      } yield response
    },

    Method.GET / "read" -> handler { (req: Request) =>
      val queryParams = req.url.queryParams
      val nameOpt = queryParams.get("name").flatMap(_.headOption).filter(_.nonEmpty)
      val ageOpt = queryParams.get("age").flatMap(_.headOption).flatMap(_.toIntOption)
      val actualWorkOpt = queryParams.get("actual_work").flatMap(_.headOption).filter(_.nonEmpty)

      (nameOpt, ageOpt, actualWorkOpt) match {
        case (Some(name), Some(age), Some(actualWork)) =>
          dbService.getUser(name, age, actualWork).flatMap {
            case Some(user) => xmlService.toXml(user).map { xml =>
              Response.text(xml).withStatus(Status.Ok).contentType(MediaType.application.xml)
            }
            case None => xmlService.errorXml("User not found").map { errXml =>
              Response.text(errXml).withStatus(Status.NotFound).contentType(MediaType.application.xml)
            }
          }
        case _ => xmlService.errorXml("Missing required parameters: name, age, or actual_work").map { errXml =>
          Response.text(errXml).withStatus(Status.BadRequest).contentType(MediaType.application.xml)
        }
      }
    }
  )
}
