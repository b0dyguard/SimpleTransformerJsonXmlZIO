package clientWebService.rest

import clientWebService.service.convert.XmlMapperService
import clientWebService.service.database.DatabaseService
import zio.http._

object AllRoutes {
  def apply(dbService: DatabaseService, xmlService: XmlMapperService): Routes[Any, Response] = {
    val convertRoute = ConvertRoute(xmlService).routes
    val dbRoutes = DatabaseRoutes(dbService, xmlService).routes

    convertRoute ++ dbRoutes
  }
}
