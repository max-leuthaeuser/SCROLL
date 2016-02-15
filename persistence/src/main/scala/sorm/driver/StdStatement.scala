package sorm.driver

import sorm.jdbc
import sorm.sql.Sql

trait StdStatement {
  def statement(sql: Sql): jdbc.Statement
}
