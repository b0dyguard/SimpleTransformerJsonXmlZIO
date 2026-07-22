package jsonXmlTransformer.model.`export`

import jsonXmlTransformer.model.units.UserRow
import zio._
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CsvFileMaker {
  def createFile(targetDir: String, users: Seq[UserRow]): Task[File] = {
    val directory = new File(targetDir)

    for {
      created <- ZIO.attemptBlocking(
        if (!directory.exists()) directory.mkdirs() else false
      )
      _ <- ZIO.when(created)(ZIO.logInfo(s"[BG] Created new directory for backups: $targetDir"))

      now = LocalDateTime.now()
      formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")
      fileName = s"${now.format(formatter)}_actual_users.csv"
      outputFile = new File(directory, fileName)

      _ <- CsvWriter.write(outputFile, users)
    } yield outputFile
  }
}
