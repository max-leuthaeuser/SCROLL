package sorm.driver

import sorm.core.SormException
import sorm.jdbc.Statement

trait StdDropAllTables {
  self: StdConnection with StdListTables with StdQuote =>
  def dropAllTables(): Unit = {
    def tryToDrop
    (table: String): Unit = {
      try {
        val _ = connection.executeUpdate(Statement("DROP TABLE " + quote(table)))
      } catch {
        case e: Throwable =>
      }
    }

    var lastTables = List[String]()
    var tables = listTables()
    while (tables != lastTables) {
      tables foreach tryToDrop
      lastTables = tables
      tables = listTables()
    }

    if (lastTables.nonEmpty) {
      throw new SormException("Couldn't drop all tables")
    }

  }
}
