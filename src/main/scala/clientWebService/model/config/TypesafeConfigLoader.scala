package clientWebService.model.config

import com.typesafe.config.ConfigFactory
import clientWebService.config.AppConfig
import zio._
import zio.json._

import scala.util.Try


object TypesafeConfigLoader {
  val live: ZLayer[Any, Throwable, AppConfig] = ZLayer {
    for {
      _      <- ZIO.logInfo("Loading configuration properties..")
      tsConf <- ZIO.attempt(ConfigFactory.load())

      port      <- ZIO.attempt(Try(tsConf.getInt("port")).getOrElse(9000))
      interval  <- ZIO.attempt(Try(tsConf.getInt("intervalMinutes")).getOrElse(1))
      dir       <- ZIO.attempt(Try(tsConf.getString("targetDirectory")).map(_.trim).filter(_.nonEmpty).getOrElse("C:/TEMP/Export"))

      appConfig = AppConfig(port, interval, dir)
      _ <- ZIO.logInfo(s"Configuration loaded successfully: ${appConfig.toJson}")
    } yield appConfig
  }
}
