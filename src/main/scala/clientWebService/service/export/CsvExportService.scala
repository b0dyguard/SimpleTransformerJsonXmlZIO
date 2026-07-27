package clientWebService.service.`export`

import clientWebService.service.config.ConfigService
import clientWebService.service.database.DatabaseService
import zio._

trait CsvExportService {
  def exporting: UIO[Fiber.Runtime[Nothing, Long]]
}

object CsvExportService {
  val live: ZLayer[ConfigService with DatabaseService, Nothing, CsvExportService] =
    ZLayer.fromFunction((cfgService: ConfigService, dbService: DatabaseService) => CsvExportServiceLive(cfgService, dbService))
}