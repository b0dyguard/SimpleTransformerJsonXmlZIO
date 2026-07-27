package clientWebService.model.mappers


import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import zio._


object JacksonXmlMapper {
  val live: ZLayer[Any, Nothing, XmlMapper] = ZLayer.succeed {
    val m = new XmlMapper()
    m.registerModule(DefaultScalaModule)
    m.enable(SerializationFeature.INDENT_OUTPUT)
    m
  }
}
