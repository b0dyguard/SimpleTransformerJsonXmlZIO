package clientWebService.storage.table

import slick.lifted.TableQuery
import clientWebService.storage.table.tables.UsersTable

object TableSchema {
  val usersQuery = TableQuery[UsersTable]
}
