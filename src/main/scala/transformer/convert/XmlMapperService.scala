package transformer.convert

import transformer.user.User
import zio._

trait XmlMapperService {
  def toXml(user: User): Task[String]
  def errorXml(message: String): String
}

object XmlMapperService {
  val live: ZLayer[Any, Nothing, XmlMapperService] = ZLayer.succeed(new XmlMapperServiceLive)
}