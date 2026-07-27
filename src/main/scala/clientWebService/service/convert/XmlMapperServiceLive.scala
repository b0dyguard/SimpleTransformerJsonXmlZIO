package clientWebService.service.convert


import com.fasterxml.jackson.dataformat.xml.XmlMapper
import clientWebService.model.units.User
import zio._


case class XmlMapperServiceLive(mapper: XmlMapper) extends XmlMapperService {

  override def toXml(user: User): Task[String] = ZIO.attempt {
    mapper.writeValueAsString(user)
  }

  override def errorXml(message: String): String = {
    s"<response>\n<status>error</status>\n<message>$message</message>\n</response>"
  }

  override def successXml(message: String): String = {
    s"<response>\n<status>success</status>\n<message>$message</message>\n</response>"
  }
}
