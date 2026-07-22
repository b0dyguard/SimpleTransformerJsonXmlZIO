package jsonXmlTransformer.storage.connection

import slick.jdbc.H2Profile.api._
import zio._

object DbConnection {
  val live: ZLayer[Any, Throwable, Database] = ZLayer.scoped {
    ZIO.acquireRelease(
      ZIO.attempt(Database.forURL(
        url = "jdbc:h2:mem:usersdb;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver",
        user = "sa",
        password = ""
      ))
    )(db => ZIO.attempt(db.close()).orDie)
  }
}
