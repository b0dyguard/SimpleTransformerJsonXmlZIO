package transformer.storage.table

import slick.lifted.TableQuery
import transformer.storage.table.tables.UsersTable

object TableSchema {
  val usersQuery = TableQuery[UsersTable]
}
