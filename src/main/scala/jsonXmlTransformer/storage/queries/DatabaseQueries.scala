package jsonXmlTransformer.storage.queries

import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.{User, UserRow}
import jsonXmlTransformer.storage.table.TableSchema._

import scala.concurrent.ExecutionContext

object DatabaseQueries {

  def init(seedUsers: Seq[UserRow]): DBIO[Unit] = {
    DBIO.seq(
      usersQuery.schema.drop.asTry,
      usersQuery.schema.create,
      usersQuery ++= seedUsers
    )
  }

  def save(user: User): DBIO[Int] = {
    val row = UserRow(
      id = None,
      name = user.name,
      age = user.age,
      actualWork = user.actualWork,
      previousWorks = if (user.previousWorks != null) user.previousWorks.mkString(", ") else "",
      currentStatusActive = user.currentStatusActive
    )
    usersQuery += row
  }

  def find(name: String, age: Int, actualWork: String)(implicit ec: ExecutionContext): DBIO[Option[User]] = {
    val userRow = usersQuery
      .filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
      .result
      .headOption

    userRow.map(_.map { row =>
      User(
        name = row.name,
        age = row.age,
        actualWork = row.actualWork,
        previousWorks = if (row.previousWorks.isEmpty) Nil else row.previousWorks.split(", ").toList,
        currentStatusActive = row.currentStatusActive
      )
    })
  }

  def listAll: DBIO[Seq[UserRow]] = usersQuery.result
}
