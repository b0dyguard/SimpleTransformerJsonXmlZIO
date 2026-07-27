package clientWebService.service.server

import clientWebService.rest.AllRoutes
import clientWebService.service.convert.XmlMapperService
import clientWebService.service.database.DatabaseService
import zio.http._


case class HttpServiceLive(dbService: DatabaseService, xmlService: XmlMapperService) extends HttpService {

  override def routes: Routes[Any, Response] = AllRoutes(dbService, xmlService)
}