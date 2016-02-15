package sorm.driver

import sorm.ddl._
import sorm.jdbc._
import sorm.sql.Sql

class Mysql(protected val connection: JdbcConnection)
  extends DriverConnection
  with StdConnection
  with StdTransaction
  with StdAbstractSqlToSql
  with StdQuote
  with StdSqlRendering
  with StdStatement
  with StdQuery
  with StdModify
  with StdCreateTable
  with StdListTables
  with StdDropTables
  with StdDropAllTables
  with StdNow {

  override protected def quote(x: String) = "`" + x + "`"

  override protected def columnTypeDdl(t: ColumnType)
  = {
    import ColumnType._
    t match {
      case Text => "LONGTEXT"
      case _ => super.columnTypeDdl(t)
    }
  }

  override protected def postProcessSql(sql: Sql.Statement) = sql
}
