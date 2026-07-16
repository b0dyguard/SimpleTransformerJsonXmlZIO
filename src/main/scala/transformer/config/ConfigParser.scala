package transformer.config

trait ConfigParser[T] {
  def parse(v: String): Either[String, T]
}

object ConfigParser {
  implicit val strParser: ConfigParser[String] = (v: String) => Right(v)
  implicit val intParser: ConfigParser[Int]    = (v: String) => v.toIntOption.toRight(s"'$v' is not a valid integer")
}
