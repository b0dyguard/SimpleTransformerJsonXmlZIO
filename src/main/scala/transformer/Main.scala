package transformer

import transformer.`export`.CsvExportService
import transformer.config.ConfigService
import transformer.convert.XmlMapperService
import transformer.db.DatabaseService
import transformer.routes.HttpService
import zio._
import zio.http._

object Main extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val appLogic = for {
      _ <- ZIO.logInfo("Initializing Database..")
      _ <- DatabaseService.init()
      _ <- ZIO.logInfo("Database successfully initialized.")

      _ <- CsvExportService.exporting()

      config <- ConfigService.getConfig
      port = config.port

      routes <- HttpService.routes
      _ <- ZIO.logInfo(s"Starting Netty HTTP Server on port $port")
      _ <- Server.serve(routes).provideSome[Scope](Server.defaultWithPort(port))
    } yield ()

    appLogic.provide(
      ConfigService.live,
      DatabaseService.live,
      XmlMapperService.live,
      CsvExportService.live,
      HttpService.live
    )
  }
}