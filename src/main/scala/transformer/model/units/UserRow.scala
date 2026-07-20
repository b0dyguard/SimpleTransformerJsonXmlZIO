package transformer.model.units

case class UserRow(
                  id: Option[Int],
                  name: String,
                  age: Int,
                  actualWork: String,
                  previousWorks: String,
                  currentStatusActive: Boolean
                  )
