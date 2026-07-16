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
      db <- ZIO.service[DatabaseService]
      _ <- db.initDb
      _ <- ZIO.logInfo("Database successfully initialized.")

      exporter <- ZIO.service[CsvExportService]
      _ <- exporter.exporting

      configService <- ZIO.service[ConfigService]
      port = configService.config.port

      httpService <- ZIO.service[HttpService]
      routes = httpService.routes
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