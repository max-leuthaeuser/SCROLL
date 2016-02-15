package sorm.mappings

object `package` {
  def ddlName(string: String)
  : String
  = {
    import com.google.common.base.CaseFormat._
    UPPER_CAMEL.to(LOWER_UNDERSCORE, string)
  }
}