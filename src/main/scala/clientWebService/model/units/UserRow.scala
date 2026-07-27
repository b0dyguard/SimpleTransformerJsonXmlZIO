package clientWebService.model.units

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

  def tupled: ((Option[Int], String, Int, String, String, Boolean)) => UserRow =
    (UserRow.apply _).tupled

}
