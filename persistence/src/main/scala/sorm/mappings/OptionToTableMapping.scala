package sorm.mappings

import sorm.driver.DriverConnection
import sorm.jdbc.ResultSetView
import sorm.reflection._

class OptionToTableMapping
(val reflection: Reflection,
 val membership: Option[Membership],
 val settings: Map[Reflection, EntitySettings])
  extends SlaveTableMapping {
  lazy val item = Mapping(reflection.generics(0), Membership.OptionToTableItem(this), settings)
  lazy val primaryKeyColumns = masterTableColumns
  lazy val generatedColumns = primaryKeyColumns
  lazy val mappings = item +: Stream()


  def parseResultSet(rs: ResultSetView, connection: DriverConnection)
  = rs.byNameRowsTraversable.toStream.headOption.map(item.valueFromContainerRow(_, connection))

  override def update(value: Any, masterKey: Stream[Any], connection: DriverConnection): Unit = {
    connection.delete(tableName, masterTableColumnNames zip masterKey)
    insert(value, masterKey, connection)
  }

  override def insert(v: Any, masterKey: Stream[Any], connection: DriverConnection): Unit = {
    v.asInstanceOf[Option[_]]
      .foreach { v =>
        val pk = masterKey
        val values = item.valuesForContainerTableRow(v) ++: (primaryKeyColumnNames zip pk)
        connection.insert(tableName, values)
        item.insert(v, pk, connection)
      }
  }

  def valuesForContainerTableRow(value: Any) = Stream()
}
