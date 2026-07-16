package transformer.routes

import transformer.convert.XmlMapperService
import transformer.db.DatabaseService
import zio._
import zio.http._
import zio.json._

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
