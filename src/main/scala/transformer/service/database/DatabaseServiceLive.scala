package transformer.service.database

import slick.jdbc.H2Profile.api._
import transformer.model.units.{User, UserRow}
import transformer.storage.schema.TableSchema._
import zio._

import scala.concurrent.ExecutionContext

class DatabaseServiceLive(db: Database) extends DatabaseService {

  override def initDb: Task[Unit] = ZIO.fromFuture { ec =>
    implicit val exec: ExecutionContext = ec
    val setup = DBIO.seq(
      usersQuery.schema.createIfNotExists,
      usersQuery.delete,
      usersQuery ++= Seq(
        UserRow(None, "Витя", 40, "УАЗ", "Улгу, Политех", currentStatusActive = true),
        UserRow(None, "Артем", 22, "Ультра", "УлГПУ, Улет", currentStatusActive = true),
        UserRow(None, "Алексей", 23, "Автозавод", "КЭИ, Яндекс-доставка", currentStatusActive = false),
        UserRow(None, "Никита", 21, "УИ ГА", "УИ ГА, Озон", currentStatusActive = true),
        UserRow(None, "Елена", 24, "Тинькофф", "УлГТУ, Альфа-Банк", currentStatusActive = true),
        UserRow(None, "Сергей", 25, "Ростелеком", "УлГУ, Билайн", currentStatusActive = false)
      )
    )
    db.run(setup)
  }.unit

  override def saveUser(user: User): Task[Unit] = ZIO.fromFuture { ec =>
    implicit val exec: ExecutionContext = ec
    val row = UserRow(
      id = None,
      name = user.name,
      age = user.age,
      actualWork = user.actualWork,
      previousWorks = if (user.previousWorks != null) user.previousWorks.mkString(", ") else "",
      currentStatusActive = user.currentStatusActive
    )
    db.run(usersQuery += row)
  }.unit

  override def findUser(name: String, age: Int, actualWork: String): Task[Option[User]] =
    ZIO.fromFuture { ec =>
      implicit val exec: ExecutionContext = ec
      val action = usersQuery
        .filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
        .result
        .headOption
      db.run(action)
    }.map(_.map { row =>
      User(
        name = row.name,
        age = row.age,
        actualWork = row.actualWork,
        previousWorks = if (row.previousWorks.isEmpty) Nil else row.previousWorks.split(", ").toList,
        currentStatusActive = row.currentStatusActive
      )
    })

  override def listAllUsers: Task[Seq[UserRow]] = ZIO.fromFuture { ec =>
    implicit val exec: ExecutionContext = ec
    db.run(usersQuery.result)
  }
}
