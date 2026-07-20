package transformer.service.`export`

import transformer.service.config.ConfigService
import transformer.service.database.DatabaseService
import zio._

trait CsvExportService {
  def exporting: UIO[Fiber.Runtime[Nothing, Long]]
}

object CsvExportService {
  val live: ZLayer[ConfigService with DatabaseService, Nothing, CsvExportService] = ZLayer {
    for {
      configService <- ZIO.service[ConfigService]
      dbService     <- ZIO.service[DatabaseService]
    } yield new CsvExportServiceLive(configService, dbService)
  }
}