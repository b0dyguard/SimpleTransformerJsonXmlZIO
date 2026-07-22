package jsonXmlTransformer.storage.table

import slick.lifted.TableQuery
import jsonXmlTransformer.storage.table.tables.UsersTable

object TableSchema {
  val usersQuery = TableQuery[UsersTable]
}
