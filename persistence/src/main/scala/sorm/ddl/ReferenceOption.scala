package sorm.ddl

sealed trait ReferenceOption

object ReferenceOption {

  case object Restrict extends ReferenceOption

  case object Cascade extends ReferenceOption

  case object NoAction extends ReferenceOption

  case object SetNull extends ReferenceOption

  case object SetDefault extends ReferenceOption

}
