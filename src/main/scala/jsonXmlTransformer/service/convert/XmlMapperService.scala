package jsonXmlTransformer.service.convert


import com.fasterxml.jackson.dataformat.xml.XmlMapper
import jsonXmlTransformer.model.units.User
import zio._


trait XmlMapperService {
  def toXml(user: User): Task[String]
  def errorXml(message: String): String
  def successXml(message: String): String
}

object XmlMapperService {
  val live: ZLayer[XmlMapper, Nothing, XmlMapperService] =
    ZLayer.fromFunction((mapper: XmlMapper) => XmlMapperServiceLive(mapper))
}