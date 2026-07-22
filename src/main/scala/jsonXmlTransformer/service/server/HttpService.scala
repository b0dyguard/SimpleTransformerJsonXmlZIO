package jsonXmlTransformer.service.server

import jsonXmlTransformer.service.convert.XmlMapperService
import jsonXmlTransformer.service.database.DatabaseService
import zio._
import zio.http._

trait HttpService {
  def routes: Routes[Any, Response]
}

object HttpService {
  val live: ZLayer[DatabaseService with XmlMapperService, Nothing, HttpService] =
    ZLayer.fromFunction((dbService: DatabaseService, xmlService: XmlMapperService) => HttpServiceLive(dbService, xmlService))
}
