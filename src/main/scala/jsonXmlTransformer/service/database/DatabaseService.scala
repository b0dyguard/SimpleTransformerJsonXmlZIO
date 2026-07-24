package jsonXmlTransformer.service.database


import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.{User, UserRow}
import zio._


trait DatabaseService {
  def initDb(seedUsers: Seq[UserRow]): Task[Unit]
  def saveUser(user: User): Task[Int]
  def findUser(name: String, age: Int, actualWork: String): Task[Option[User]]
  def listAllUsers: Task[Seq[UserRow]]
  def updateUser(oldName: String, oldAge: Int, oldWork: String, newUser: UserRow): Task[Int]
  def deleteUser(name: String, age: Int, actualWork: String): Task[Int]
}

object DatabaseService {
  val live: ZLayer[Database, Nothing, DatabaseService] = ZLayer.fromFunction((db: Database) => DatabaseServiceLive(db))
}
