package transformer.rest.routes.convertRoute

import transformer.model.units.User
import transformer.service.convert.XmlMapperService
import zio.ZIO
import zio.http._
import zio.json._

class ConvertRoute(xmlService: XmlMapperService) {
  val route: Routes[Any, Response] = Routes(
    Method.POST / "convert" -> handler { (req: Request) =>
      for {
        bodyStr <- req.body.asString.orDie
        userDefault = bodyStr.fromJson[User]
        response <- userDefault match {
          case Left(err) => ZIO.succeed {
            val errXml = xmlService.errorXml(s"Invalid JSON: $err")
            Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
          }
          case Right(user) => if (user.name.equalsIgnoreCase("Виталий")) {
            ZIO.succeed {
              val errXml = xmlService.errorXml(s"This user does not exist.")
              Response(status = Status.BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
            }
          } else {
            xmlService.toXml(user).map { xml =>
              Response(status = Status.Ok, body = Body.fromString(xml).contentType(MediaType.application.xml))
            }.catchAll { err =>
              ZIO.succeed(Response(status = Status.InternalServerError, body = Body.fromString(s"Error: ${err.getMessage}")))
            }
          }
        }
      } yield response
    }
  )
}

object ConvertRoute {
  def apply(xmlService: XmlMapperService) = new ConvertRoute(xmlService)
}
