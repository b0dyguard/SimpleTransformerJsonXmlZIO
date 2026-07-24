package jsonXmlTransformer.storage.queries

import jsonXmlTransformer.config.QueriesConfig
import jsonXmlTransformer.model.units.UserRow.toUser
import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.{User, UserRow}
import jsonXmlTransformer.storage.table.TableSchema._
import zio._
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseQueries {
  val live: ZLayer[Database, Throwable, QueriesConfig] = ZLayer {
    for {
      db <- ZIO.service[Database]
      _ <- ZIO.logInfo("Initializing queries..")

      init = ZIO.fromFuture { _ =>
        db.run(
          DBIO.seq(
            usersQuery.schema.drop.asTry,
            usersQuery.schema.create,
            usersQuery ++= seedData.SeedUsers.users
          )
        )
      }

      save = (user: User) => {
        val row = UserRow(
          None,
          user.name,
          user.age,
          user.actualWork,
          if (user.previousWorks != null) user.previousWorks.mkString(", ") else "",
          user.currentStatusActive
        )
        ZIO.fromFuture(_ => db.run(usersQuery += row))
      }

      find = (name: String, age: Int, actualWork: String) => ZIO.fromFuture { _ =>
        db.run(usersQuery
          .filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
          .result
          .headOption
          ).map(_.map(toUser))
      }

      listAll = ZIO.fromFuture(_ => db.run(usersQuery.result))

      update = (name: String, age: Int, actualWork: String, newUser: UserRow) =>
        ZIO.fromFuture { _ =>
          db.run(usersQuery
            .filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
            .map(u => (u.name, u.age, u.actualWork, u.previousWorks, u.currentStatusActive))
            .update((newUser.name, newUser.age, newUser.actualWork, newUser.previousWorks, newUser.currentStatusActive)))
        }

      delete = (name: String, age: Int, actualWork: String) =>
        ZIO.fromFuture { _ =>
          db.run(usersQuery
          .filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
          .delete)
        }

    } yield QueriesConfig(init, save, find, listAll, update, delete)
  }
}
