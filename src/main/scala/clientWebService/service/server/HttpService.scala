package clientWebService.service.server

import clientWebService.service.convert.XmlMapperService
import clientWebService.service.database.DatabaseService
import zio._
import zio.http._

trait HttpService {
  def routes: Routes[Any, Response]
}

object HttpService {
  val live: ZLayer[DatabaseService with XmlMapperService, Nothing, HttpService] =
    ZLayer.fromFunction((dbService: DatabaseService, xmlService: XmlMapperService) => HttpServiceLive(dbService, xmlService))
}
