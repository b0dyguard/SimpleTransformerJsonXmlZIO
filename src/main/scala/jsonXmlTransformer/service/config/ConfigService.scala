package jsonXmlTransformer.service.config

import jsonXmlTransformer.config.AppConfig
import zio._


trait ConfigService {
  def config: AppConfig
}

object ConfigService {
  val live: ZLayer[AppConfig, Nothing, ConfigService] = ZLayer.fromFunction((config: AppConfig) => ConfigServiceLive(config))
}
