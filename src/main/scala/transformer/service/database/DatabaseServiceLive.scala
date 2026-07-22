package transformer.service.database


import slick.jdbc.H2Profile.api._
import transformer.model.units.{User, UserRow}
import transformer.storage.queries.DatabaseQueries
import zio._


case class DatabaseServiceLive(db: Database) extends DatabaseService {

  override def initDb(seedUsers: Seq[UserRow]): Task[Unit] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.init(seedUsers)))

  override def saveUser(user: User): Task[Unit] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.save(user))).unit

  override def findUser(name: String, age: Int, actualWork: String): Task[Option[User]] =
    ZIO.fromFuture(implicit ec => db.run(DatabaseQueries.find(name, age, actualWork)))

  override def listAllUsers: Task[Seq[UserRow]] =
    ZIO.fromFuture(_ => db.run(DatabaseQueries.listAll))

}
