package transformer.service.config

import transformer.config.app.AppConfig
import zio._

import java.io.FileNotFoundException
import java.util.Properties                         //использовать typesafe config.

trait ConfigService {
  def config: AppConfig
}

object ConfigService {
  val live: ZLayer[Any, Throwable, ConfigService] = ZLayer {
    for {
      _ <- ZIO.logInfo("Loading configuration properties..")
      prop <- ZIO.acquireReleaseWith(
        ZIO.attempt(Option(getClass.getClassLoader.getResourceAsStream("application.conf")))
      )(isOpt => ZIO.attempt(isOpt.foreach(_.close())).orDie) {
        case Some(is) => ZIO.attempt {
          val prop = new Properties()
          prop.load(is)
          prop
        }
        case None => ZIO.fail(new FileNotFoundException("'application.conf' not found"))
      }

      port = Option(prop.getProperty("port")).flatMap(_.trim.toIntOption).getOrElse(9000)
      interval = Option(prop.getProperty("intervalMinutes")).flatMap(_.trim.toIntOption).getOrElse(1)
      dir = Option(prop.getProperty("targetDirectory")).map(_.trim.replace("\"", "")).getOrElse("C:/TEMP/Export")

      appConfig = AppConfig(port, interval, dir)
      _ <- ZIO.logInfo(
        s"""\n
            Configuration loaded success:
               port:            $port
               intervalMinutes: $interval
               targetDirectory: $dir
            \n""".stripMargin
      )
    } yield ConfigServiceLive(appConfig)
  }
}
