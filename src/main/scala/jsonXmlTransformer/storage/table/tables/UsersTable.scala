package jsonXmlTransformer.storage.table.tables


import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.UserRow


class UsersTable(tag: slick.lifted.Tag) extends Table[UserRow](tag, "users") {
  private def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def age = column[Int]("age")
  def actualWork = column[String]("actual_work")
  private def previousWorks = column[String]("previous_works")
  private def currentStatusActive = column[Boolean]("current_status_active")

  def * = (id.?, name, age, actualWork, previousWorks, currentStatusActive) <> (UserRow.tupled, UserRow.unapply)
}