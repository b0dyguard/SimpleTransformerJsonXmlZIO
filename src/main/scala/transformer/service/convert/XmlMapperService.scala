package transformer.service.convert


import com.fasterxml.jackson.dataformat.xml.XmlMapper
import transformer.model.units.User
import zio._


trait XmlMapperService {
  def toXml(user: User): Task[String]
  def errorXml(message: String): String
}

object XmlMapperService {
  val live: ZLayer[XmlMapper, Nothing, XmlMapperService] =
    ZLayer.fromFunction((mapper: XmlMapper) => XmlMapperServiceLive(mapper))
}