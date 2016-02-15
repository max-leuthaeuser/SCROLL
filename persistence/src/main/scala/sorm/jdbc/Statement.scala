package sorm.jdbc

case class Statement
(sql: String,
 data: Seq[JdbcValue] = Nil)

object Statement {
  def simple(sql: String, data: Seq[Any] = Nil)
  = Statement(sql, data.map(JdbcValue(_)))
}
