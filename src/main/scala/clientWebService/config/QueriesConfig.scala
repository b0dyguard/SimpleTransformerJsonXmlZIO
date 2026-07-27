package clientWebService.config

import clientWebService.model.units.{User, UserRow}
import slick.jdbc.H2Profile.api._
import zio._

case class QueriesConfig(
                          init: Task[Unit],
                          save: User => Task[Int],
                          find: (String, Int, String) => Task[Option[User]],
                          listAll: Task[Seq[UserRow]],
                          update: (String, Int, String, UserRow) => Task[Int],
                          delete: (String, Int, String) => Task[Int]
                        )
