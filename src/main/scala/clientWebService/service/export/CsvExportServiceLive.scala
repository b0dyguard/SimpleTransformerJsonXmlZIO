package clientWebService.service.`export`

import clientWebService.model.`export`.CsvExporter
import clientWebService.service.config.ConfigService
import clientWebService.service.database.DatabaseService
import zio._

case class CsvExportServiceLive(configService: ConfigService, databaseService: DatabaseService) extends CsvExportService {

  override def exporting: UIO[Fiber.Runtime[Nothing, Long]] = CsvExporter.exporting(configService, databaseService)
}
