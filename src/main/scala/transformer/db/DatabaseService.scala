package transformer.db

import zio._
import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext
import transformer.user.User

trait DatabaseService {
  def initDb: Task[Unit]
  def saveUser(user: User): Task[Unit]
  def findUser(name: String, age: Int, actualWork: String): Task[Option[User]]
  def listAllUsers: Task[Seq[UserRow]]
}

object DatabaseService {
  val dbLayer: ZLayer[Any, Throwable, Database] = ZLayer.scoped {
    ZIO.acquireRelease(
      ZIO.attempt(Database.forURL(
        url = "jdbc:h2:mem:usersdb;DB_CLOSE_DELAY=-1",
        driver = "ord.h2.Driver",
        user = "sa",
        password = ""
      ))
    )(db => ZIO.attempt(db.close()).orDie)
  }

  val live: ZLayer[Any, Throwable, DatabaseService] = dbLayer >>> ZLayer {
    for {
      db <- ZIO.service[Database]
    } yield new DatabaseServiceLive(db)
  }
}
