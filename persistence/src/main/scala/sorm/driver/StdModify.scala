package sorm.driver

import embrace._
import sorm.sql.Sql._

trait StdModify {
  self: StdConnection with StdStatement =>

  def update
  (table: String, values: Iterable[(String, Any)], pk: Iterable[(String, Any)]): Unit = {
    val exprs = values.toStream.map { case (c, v) => SetExpression(Column(c), v) }
    if (exprs.nonEmpty) {
      val _ = Update(table, exprs, pk $ where) $ statement $ connection.executeUpdate
    }
  }

  def insert
  (table: String, values: Iterable[(String, Any)]): Unit = {
    val _ = values.toStream.unzip $$ (Insert(table, _, _)) $ statement $ connection.executeUpdate
  }

  def insertAndGetGeneratedKeys
  (table: String, values: Iterable[(String, Any)])
  : Seq[Any]
  = values.toStream.unzip $$ (Insert(table, _, _)) $ statement $ connection.executeUpdateAndGetGeneratedKeys $ (_.head)

  def delete
  (table: String, pk: Iterable[(String, Any)]): Unit = {
    val _ = Delete(table, pk $ where) $ statement $ connection.executeUpdate
  }

  private def where(pk: Iterable[(String, Any)])
  = pk.view map (_ $$ (Column(_) -> Value(_)) $$ (Comparison(_, _, Equal): Condition[WhereObject])) reduceOption (CompositeCondition(_, _, And)) map Where
}
