package sorm.mappings

import embrace._
import sorm.ddl._
import sorm.driver.DriverConnection
import sorm.reflection._

class EnumMapping
(val reflection: Reflection,
 val membership: Option[Membership],
 val settings: Map[Reflection, EntitySettings])
  extends ColumnMapping {
  lazy val dbValues: Map[Enumeration#Value, Short]
  = values.map(_.swap)
  private lazy val values: Map[Short, Enumeration#Value]
  = reflection.containerObject.get.asInstanceOf[Enumeration].values
    .view.map(v => v.id.toShort -> v).toMap

  def columnType
  = ColumnType.SmallInt

  def valueFromContainerRow(data: String => Any, connection: DriverConnection)
  = data(memberName).asInstanceOf[Short] $ values

  def valuesForContainerTableRow(value: Any)
  = value.asInstanceOf[Enumeration#Value] $ dbValues $ (memberName -> _) $ (Stream(_))

}