package transformer

import transformer.`export`.CsvExportService
import transformer.config.ConfigService
import transformer.convert.XmlMapperService
import transformer.db.DatabaseService
import transformer.routes.HttpService
import zio._
import zio.http._
import zio.logging.backend.SLF4J

object Boot extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    val program = for {
      _ <- ZIO.logInfo("Initializing application components..")

      dbService <- ZIO.service[DatabaseService]
      _ <- dbService.initDb
      _ <- ZIO.logInfo("Database successfully initialized.")

      csvService <- ZIO.service[CsvExportService]
      _ <- csvService.exporting
      _ <- ZIO.logInfo("CSV Export service successfully started.")

      configService <- ZIO.service[ConfigService]
      port = configService.config.port

      httpService  <- ZIO.service[HttpService]
      _            <- ZIO.logInfo(s"Starting HTTP Server on port $port..")
      serverConfig = Server.Config.default.port(port)
      _            <- Server.serve(httpService.routes).provideSome[Scope](
        Server.live,
        ZLayer.succeed(serverConfig)
      )
    } yield ()

    program.provideSome[Scope](
      ConfigService.live,
      DatabaseService.live,
      XmlMapperService.live,
      CsvExportService.live,
      HttpService.live
    )
  }
}