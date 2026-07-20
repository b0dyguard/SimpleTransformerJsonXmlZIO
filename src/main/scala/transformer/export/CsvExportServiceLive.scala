package transformer.`export`

import transformer.config.ConfigService
import transformer.db.DatabaseService
import transformer.user.UserRow

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import zio._

import java.io.{File, FileWriter}

class CsvExportServiceLive(configService: ConfigService, dbService: DatabaseService) extends CsvExportService {

  override def exporting: UIO[Fiber.Runtime[Nothing, Long]] = {
    val interval = configService.config.intervalMinutes.minutes

    val exportTask = for {
      _     <- ZIO.logInfo("[BG] Exporting data to CSV..")
      users <- dbService.listAllUsers
      _     <- writeCsvFile(users)
    } yield ()

    exportTask
      .catchAll(err => ZIO.logError(s"[BG] Error during background CSV export: ${err.getMessage}"))
      .repeat(Schedule.spaced(interval))
      .forkDaemon
  }


  private def writeCsvFile(users: Seq[UserRow]): Task[Unit] = ZIO.attempt {
    val targetDirStr = configService.config.targetDirectory
    val directory = new File(targetDirStr)
    val targetDirectoryCorrected = targetDirStr.replace('/', '\\')

    if (!directory.exists()) {
      val created = directory.mkdirs()
      if (created) ZIO.logInfo(s"[BG] Created new directory for backups: $targetDirectoryCorrected")
    }

    val now = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")
    val fileName = s"${now.format(formatter)}_actual_users.csv"
    val outputFile = new File(directory, fileName)

    val writer = new FileWriter(outputFile, false)
    try {
      writer.append("id,name,age,actual_work,previous_works,current_status_active\n")
      users.foreach { u =>
        writer.append(s"${u.id.getOrElse("")},")
        writer.append(s"${u.name},")
        writer.append(s"${u.age},")
        writer.append(s"${u.actualWork},")

        if(u.previousWorks != null && u.previousWorks.nonEmpty) {
          writer.append(s""""${u.previousWorks}",""")
        } else {
          writer.append(",")
        }
        writer.append(s"${u.currentStatusActive}\n")
      }
      ZIO.logInfo(s"[BG] Data successfully uploaded to file: ${outputFile.getAbsolutePath}")
    } finally {
      writer.close()
    }
  }
}
