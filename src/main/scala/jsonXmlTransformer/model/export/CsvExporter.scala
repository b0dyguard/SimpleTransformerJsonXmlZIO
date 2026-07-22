package jsonXmlTransformer.model.`export`

import jsonXmlTransformer.service.config.ConfigService
import jsonXmlTransformer.service.database.DatabaseService
import zio._

object CsvExporter {
  def exporting(configService: ConfigService, databaseService: DatabaseService): UIO[Fiber.Runtime[Nothing, Long]] = {
    val interval = configService.config.intervalMinutes.minutes
    val targetDirStr = configService.config.targetDirectory

    val exportTask = for {
      _ <- ZIO.logInfo("[BG] Exporting data to CSV..")
      users <- databaseService.listAllUsers
      file <- CsvFileMaker.createFile(targetDirStr, users)
      _ <- ZIO.logInfo(s"[BG] Data successfully uploaded to file: ${file.getAbsolutePath}")
    } yield ()

    exportTask
      .catchAll(err => ZIO.logError(s"[BG] Error during background CSV export: ${err.getMessage}"))
      .repeat(Schedule.spaced(interval))
      .forkDaemon
  }

}
