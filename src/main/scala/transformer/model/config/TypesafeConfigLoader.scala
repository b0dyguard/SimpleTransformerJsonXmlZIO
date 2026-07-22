package transformer.model.config

import com.typesafe.config.ConfigFactory
import transformer.config.AppConfig
import zio._
import zio.json._

import scala.util.Try


object TypesafeConfigLoader {
  val live: ZLayer[Any, Throwable, AppConfig] = ZLayer {
    for {
      _ <- ZIO.logInfo("Loading configuration properties..")
      tsConf <- ZIO.attempt(ConfigFactory.load())

      port = Try(tsConf.getInt("port")).getOrElse(9000)
      interval = Try(tsConf.getInt("intervalMinutes")).getOrElse(1)
      dir = Try(tsConf.getString("targetDirectory")).map(_.trim).filter(_.nonEmpty).getOrElse("C:/TEMP/Export")

      appConfig = AppConfig(port, interval, dir)
      _ <- ZIO.logInfo(s"Configuration loaded successfully: ${appConfig.toJson}")
    } yield appConfig
  }
}
