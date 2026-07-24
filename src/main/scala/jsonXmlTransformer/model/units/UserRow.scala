package jsonXmlTransformer.model.units

case class UserRow(
                  id: Option[Int],
                  name: String,
                  age: Int,
                  actualWork: String,
                  previousWorks: String,
                  currentStatusActive: Boolean
                  )

object UserRow {
  def toUser(userRow: UserRow): User = User(
    userRow.name,
    userRow.age,
    userRow.actualWork,
    if (userRow.previousWorks.isEmpty) Nil else userRow.previousWorks.split(", ").toList,
    userRow.currentStatusActive
  )
}
