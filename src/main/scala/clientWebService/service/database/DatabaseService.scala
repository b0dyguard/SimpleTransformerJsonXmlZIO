package clientWebService.service.database


import clientWebService.config.QueriesConfig
import clientWebService.model.units.{User, UserRow}
import zio._


trait DatabaseService {
  def initDb: Task[Unit]
  def saveUser(user: User): Task[Int]
  def findUser(name: String, age: Int, actualWork: String): Task[Option[User]]
  def listAllUsers: Task[Seq[UserRow]]
  def updateUser(oldName: String, oldAge: Int, oldWork: String, newUser: UserRow): Task[Int]
  def deleteUser(name: String, age: Int, actualWork: String): Task[Int]
}

object DatabaseService {
  val live: ZLayer[QueriesConfig, Nothing, DatabaseService] = ZLayer.fromFunction((queries: QueriesConfig) => DatabaseServiceLive(queries))
}
