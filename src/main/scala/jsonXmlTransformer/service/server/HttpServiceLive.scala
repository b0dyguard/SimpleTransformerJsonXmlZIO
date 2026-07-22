package jsonXmlTransformer.service.server

import jsonXmlTransformer.rest.AllRoutes
import jsonXmlTransformer.service.convert.XmlMapperService
import jsonXmlTransformer.service.database.DatabaseService
import zio.http._


case class HttpServiceLive(dbService: DatabaseService, xmlService: XmlMapperService) extends HttpService {

  override def routes: Routes[Any, Response] = AllRoutes(dbService, xmlService)
}