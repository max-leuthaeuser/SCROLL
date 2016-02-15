package sorm.mappings

import sorm.ddl._

trait ColumnMapping extends Mapping {
  def columnsForContainer = column +: Stream()

  lazy val column
  = Column(memberName, columnType, autoIncremented, nullable)

  lazy val nullable
  = ancestors
    .takeWhile(!_.isInstanceOf[TableMapping])
    .exists(_.isInstanceOf[OptionToNullableMapping])

  def columnType: ColumnType

  lazy val autoIncremented: Boolean
  = membership match {
    case Some(Membership.EntityId(_)) => true
    case _ => false
  }

}