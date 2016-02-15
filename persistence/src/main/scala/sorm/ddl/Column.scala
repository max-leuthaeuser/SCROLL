package sorm.ddl

case class Column
(name: String,
 t: ColumnType,
 autoIncrement: Boolean = false,
 nullable: Boolean = false)
