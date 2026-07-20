package transformer.storage.schema

import slick.lifted.TableQuery
import transformer.storage.schema.tables.UsersTable

object TableSchema {
  val usersQuery = TableQuery[UsersTable]
}
