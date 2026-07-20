package transformer.service.server

import transformer.service.convert.XmlMapperService
import transformer.service.database.DatabaseService
import zio._
import zio.http._

trait HttpService {
  def routes: Routes[Any, Response]
}

object HttpService {
  val live: ZLayer[DatabaseService with XmlMapperService, Nothing, HttpService] = ZLayer {
    for {
      dbService  <- ZIO.service[DatabaseService]
      xmlService <- ZIO.service[XmlMapperService]
    } yield new HttpServiceLive(dbService, xmlService)
  }
}
