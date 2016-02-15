package sorm.driver

import org.joda.time.DateTime
import sorm._
import sorm.abstractSql.AbstractSql._
import sorm.jdbc.ResultSetView

/**
  * An abstraction over jdbc connection, instances of which implement sql dialects of different databases
  */
trait DriverConnection {
  def query
  [T]
  (asql: Statement)
  (parse: ResultSetView => T = (_: ResultSetView).indexedRowsTraversable.toList)
  : T

  def queryJdbc
  [T]
  (s: jdbc.Statement)
  (parse: ResultSetView => T = (_: ResultSetView).indexedRowsTraversable.toList)
  : T

  def now(): DateTime

  def listTables(): List[String]

  def dropTable
  (table: String): Unit

  def dropAllTables(): Unit

  def update
  (table: String, values: Iterable[(String, Any)], pk: Iterable[(String, Any)]): Unit

  def insert
  (table: String, values: Iterable[(String, Any)]): Unit

  def insertAndGetGeneratedKeys
  (table: String, values: Iterable[(String, Any)])
  : Seq[Any]

  def delete
  (table: String, pk: Iterable[(String, Any)]): Unit

  def transaction[T](f: => T): T

  def createTable(table: ddl.Table): Unit
}
