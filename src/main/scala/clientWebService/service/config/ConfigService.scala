package clientWebService.service.config

import clientWebService.config.AppConfig
import zio._


trait ConfigService {
  def config: AppConfig
}

object ConfigService {
  val live: ZLayer[AppConfig, Nothing, ConfigService] = ZLayer.fromFunction((config: AppConfig) => ConfigServiceLive(config))
}
