package transformer.service.server

import transformer.rest.AllRoutes
import transformer.service.convert.XmlMapperService
import transformer.service.database.DatabaseService
import zio.http._


case class HttpServiceLive(dbService: DatabaseService, xmlService: XmlMapperService) extends HttpService {

  override def routes: Routes[Any, Response] = AllRoutes(dbService, xmlService)
}