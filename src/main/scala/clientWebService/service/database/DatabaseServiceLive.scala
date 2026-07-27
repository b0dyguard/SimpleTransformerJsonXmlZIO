package clientWebService.service.database


import clientWebService.config.QueriesConfig
import clientWebService.model.units.{User, UserRow}
import zio._


case class DatabaseServiceLive(queries: QueriesConfig) extends DatabaseService {

  override def initDb: Task[Unit] =
    queries.init

  override def saveUser(user: User): Task[Int] =
    queries.save(user)

  override def findUser(name: String, age: Int, actualWork: String): Task[Option[User]] =
    queries.find(name, age, actualWork)

  override def listAllUsers: Task[Seq[UserRow]] =
    queries.listAll

  override def updateUser(oldName: String, oldAge: Int, oldWork: String, newUser: UserRow): Task[Int] =
    queries.update(oldName, oldAge, oldWork, newUser)

  override def deleteUser(name: String, age: Int, work: String): Task[Int] =
    queries.delete(name, age, work)
}
