package jsonXmlTransformer.storage.table.tables


import slick.jdbc.H2Profile.api._
import jsonXmlTransformer.model.units.UserRow
import slick.lifted.Tag


class UsersTable(tag: Tag) extends Table[UserRow](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def age = column[Int]("age")
  def actualWork = column[String]("actual_work")
  def previousWorks = column[String]("previous_works")
  def currentStatusActive = column[Boolean]("current_status_active")

  def * = (id.?, name, age, actualWork, previousWorks, currentStatusActive) <> (UserRow.tupled, UserRow.unapply)
}