package transformer.model.`export`

import transformer.model.units.UserRow
import zio._
import java.io.{File, FileWriter}


object CsvWriter {
  def write(outputFile: File, users: Seq[UserRow]): Task[Unit] = {

    ZIO.acquireReleaseWith(
      ZIO.attemptBlocking(new FileWriter(outputFile, false))
    )(writer => ZIO.attemptBlocking(writer.close()).ignore) { writer =>
      ZIO.attemptBlocking {
        writer.append("id,name,age,actual_work,previous_works,current_status_active\n")
        users.foreach { u =>
          writer.append(s"${u.id.getOrElse("")},")
          writer.append(s"${u.name},")
          writer.append(s"${u.age},")
          writer.append(s"${u.actualWork},")

          if (u.previousWorks != null && u.previousWorks.nonEmpty) writer.append(s""""${u.previousWorks}",""")
          else writer.append(",")
          writer.append(s"${u.currentStatusActive}\n")
        }
      }
    }
  }
}