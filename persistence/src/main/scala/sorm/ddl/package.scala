package sorm.ddl

import sext._, embrace._

object `package` {

  case class Table
  (name: String,
   columns: Seq[Column],
   primaryKey: Seq[String],
   uniqueKeys: Set[Seq[String]] = Set.empty,
   indexes: Set[Seq[String]] = Set.empty,
   foreignKeys: Set[ForeignKey] = Set.empty)

  case class ForeignKey
  (table: String,
   bindings: Seq[(String, String)],
   onDelete: ReferenceOption = ReferenceOption.NoAction,
   onUpdate: ReferenceOption = ReferenceOption.NoAction)

  sealed trait ReferenceOption

  object ReferenceOption {

    case object Restrict extends ReferenceOption

    case object Cascade extends ReferenceOption

    case object NoAction extends ReferenceOption

    case object SetNull extends ReferenceOption

    case object SetDefault extends ReferenceOption

  }

  case class Column
  (name: String,
   t: ColumnType,
   autoIncrement: Boolean = false,
   nullable: Boolean = false)

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

}