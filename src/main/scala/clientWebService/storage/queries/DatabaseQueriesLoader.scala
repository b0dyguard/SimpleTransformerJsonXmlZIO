package clientWebService.storage.queries

import clientWebService.config.QueriesConfig
import clientWebService.model.units.User.toUserRow
import clientWebService.model.units.UserRow.toUser
import clientWebService.model.units.{User, UserRow}
import clientWebService.storage.table.TableSchema._
import slick.jdbc.H2Profile.api._
import zio._

object DatabaseQueriesLoader {
  val live: ZLayer[Database, Throwable, QueriesConfig] = ZLayer {
    for {
      db <- ZIO.service[Database]
      _ <- ZIO.logInfo("Initializing queries..")

      init = ZIO.fromFuture { _ => db.run(
          DBIO.seq(
            usersQuery.schema.drop.asTry,
            usersQuery.schema.create,
            usersQuery ++= seedData.SeedUsers.users
          )
        )
      }

      save = (user: User) => {
        val userRow = toUserRow(user)
        ZIO.fromFuture(_ => db.run(usersQuery += userRow))
      }

      find = (name: String, age: Int, actualWork: String) => ZIO.fromFuture { implicit ec =>
        db.run {
          usersQuery.filter(u => u.name === name && u.age === age && u.actualWork === actualWork)
            .result
            .headOption
            .map(_.map(toUser))
        }
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
