package transformer.service.`export`

import transformer.model.`export`.CsvExporter
import transformer.service.config.ConfigService
import transformer.service.database.DatabaseService
import zio._

case class CsvExportServiceLive(configService: ConfigService, databaseService: DatabaseService) extends CsvExportService {

  override def exporting: UIO[Fiber.Runtime[Nothing, Long]] = CsvExporter.exporting(configService, databaseService)
}
