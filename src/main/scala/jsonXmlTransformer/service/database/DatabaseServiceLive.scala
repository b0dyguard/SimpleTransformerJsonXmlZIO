package jsonXmlTransformer.service.database


import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.{User, UserRow}
import jsonXmlTransformer.storage.queries.DatabaseQueries
import zio._


case class DatabaseServiceLive(db: Database) extends DatabaseService {

  override def initDb(seedUsers: Seq[UserRow]): Task[Unit] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.init(seedUsers)))

  override def saveUser(user: User): Task[Unit] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.save(user))).unit

  override def findUser(name: String, age: Int, actualWork: String): Task[Option[User]] =
    ZIO.blocking(ZIO.fromFuture(implicit ec => db.run(DatabaseQueries.find(name, age, actualWork)(ec))))

  override def listAllUsers: Task[Seq[UserRow]] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.listAll))

  override def updateUser(id: RuntimeFlags, user: User): Task[Boolean] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.update(id, user))).map(_ > 0)

  override def deleteUser(id: RuntimeFlags): Task[Boolean] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.delete(id))).map(_ > 0)
}
