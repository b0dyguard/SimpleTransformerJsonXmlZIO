package jsonXmlTransformer.rest

import jsonXmlTransformer.model.units.User
import jsonXmlTransformer.service.convert.XmlMapperService
import zio.ZIO
import zio.http.Status._
import zio.http._
import zio.json._


case class ConvertRoute(xmlService: XmlMapperService) {
  val routes: Routes[Any, Response] = Routes(
    Method.POST / "convert" -> handler { (req: Request) =>
      for {
        bodyStr <- req.body.asString.orDie
        userDefault = bodyStr.fromJson[User]
        response <- userDefault match {
          case Left(err) => ZIO.succeed {
            val errXml = xmlService.errorXml(s"Invalid JSON: $err")
            Response(BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
          }
          case Right(user) => if (user.name.equalsIgnoreCase("Виталий")) {
            ZIO.succeed {
              val errXml = xmlService.errorXml(s"This user does not exist.")
              Response(BadRequest, body = Body.fromString(errXml).contentType(MediaType.application.xml))
            }
          } else {
            xmlService.toXml(user).map { xml =>
              Response(Ok, body = Body.fromString(xml).contentType(MediaType.application.xml))
            }.catchAll { err =>
              ZIO.succeed(Response(InternalServerError, body = Body.fromString(s"Error: ${err.getMessage}")))
            }
          }
        }
      } yield response
    }
  )
}