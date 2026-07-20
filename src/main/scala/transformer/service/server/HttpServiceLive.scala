package transformer.service.server

import transformer.rest.routes._
import transformer.rest.routes.convertRoute.ConvertRoute
import transformer.rest.routes.databaseRoutes.DatabaseRoutes
import transformer.service.convert.XmlMapperService
import transformer.service.database.DatabaseService
import zio.http._

class HttpServiceLive(dbService: DatabaseService, xmlService: XmlMapperService) extends HttpService {

  private val convertRoute = ConvertRoute(xmlService).route
  private val userRoutes = DatabaseRoutes(dbService, xmlService).routes

  override def routes: Routes[Any, Response] = convertRoute ++ userRoutes
}