package jsonXmlTransformer.storage.connection

import slick.jdbc.H2Profile.api._
import zio._

object DatabaseConnection {
  val live: ZLayer[Any, Throwable, Database] = ZLayer.scoped {
    ZIO.acquireRelease(
      ZIO.attempt(Database.forConfig("db"))
    )(db => ZIO.attempt(db.close()).orDie)
  }
}
