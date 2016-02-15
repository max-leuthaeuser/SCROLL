package sorm.ddl

sealed trait ColumnType

object ColumnType {

  case class Enum(values: Seq[String]) extends ColumnType

  case object Time extends ColumnType

  case object Date extends ColumnType

  case object TimeStamp extends ColumnType

  case object Integer extends ColumnType

  case object VarChar extends ColumnType

  case object Double extends ColumnType

  case object Float extends ColumnType

  case object Text extends ColumnType

  case object BigInt extends ColumnType

  case object Boolean extends ColumnType

  case object Decimal extends ColumnType

  case object SmallInt extends ColumnType

  case object TinyInt extends ColumnType

}