package clientWebService.config

import zio.json._

case class AppConfig(port: Int, intervalMinutes: Int, targetDirectory: String)


object AppConfig {
  implicit val encoder: JsonEncoder[AppConfig] = DeriveJsonEncoder.gen[AppConfig]
}