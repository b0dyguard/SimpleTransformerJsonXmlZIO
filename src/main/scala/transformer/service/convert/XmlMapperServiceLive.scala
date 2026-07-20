package transformer.service.convert

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import transformer.model.units.User
import zio._

class XmlMapperServiceLive extends XmlMapperService {

  private val mapper = {
    val m = new XmlMapper()
    m.registerModule(DefaultScalaModule)
    m.enable(SerializationFeature.INDENT_OUTPUT)
    m
  }

  override def toXml(user: User): Task[String] = ZIO.attempt {
    mapper.writeValueAsString(user)
  }

  override def errorXml(message: String): String = {
    s"<response><error>$message</error></response>"
  }
}
