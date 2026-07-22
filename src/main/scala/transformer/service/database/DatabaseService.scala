package transformer.service.database


import slick.jdbc.H2Profile.api._
import transformer.model.units.{User, UserRow}
import zio._


trait DatabaseService {
  def initDb(seedUsers: Seq[UserRow]): Task[Unit]
  def saveUser(user: User): Task[Unit]
  def findUser(name: String, age: Int, actualWork: String): Task[Option[User]]
  def listAllUsers: Task[Seq[UserRow]]
}

object DatabaseService {
  val live: ZLayer[Database, Nothing, DatabaseService] = ZLayer.fromFunction((db: Database) => DatabaseServiceLive(db))
}
