package sorm.driver

import embrace._
import sorm.jdbc.Statement

trait StdDropTables {
  self: StdConnection with StdQuote =>
  def dropTable(table: String): Unit = {
    val _ = table $ ("DROP TABLE " + quote(_)) $ (Statement(_)) $ connection.executeUpdate
  }
}
