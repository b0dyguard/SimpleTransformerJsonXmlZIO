package jsonXmlTransformer.service.`export`

import jsonXmlTransformer.service.config.ConfigService
import jsonXmlTransformer.service.database.DatabaseService
import zio._

trait CsvExportService {
  def exporting: UIO[Fiber.Runtime[Nothing, Long]]
}

object CsvExportService {
  val live: ZLayer[ConfigService with DatabaseService, Nothing, CsvExportService] =
    ZLayer.fromFunction((cfgService: ConfigService, dbService: DatabaseService) => CsvExportServiceLive(cfgService, dbService))
}