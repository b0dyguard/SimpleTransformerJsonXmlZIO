package jsonXmlTransformer.model.units

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.{JacksonXmlElementWrapper, JacksonXmlProperty}
import zio.json._

case class User(
               name: String,
               age: Int,

               @jsonField("actual_work")
               @JsonProperty("actual_work")
               actualWork: String,

               @jsonField("previous_works")
               @JsonProperty("previous_works")
               @JacksonXmlElementWrapper(localName = "previous_works")
               @JacksonXmlProperty(localName = "work")
               previousWorks: List[String],

               @jsonField("current_status_active")
               @JsonProperty("current_status_active")
               currentStatusActive: Boolean
               )

object User {
  implicit val codec: JsonCodec[User] = DeriveJsonCodec.gen[User]
}

