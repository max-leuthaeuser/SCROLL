package sorm.sql

import embrace._
import sorm.sql.Sql._

object Optimization {
  def optimized(s: Statement): Statement
  = s match {
    case Union(l, r) => Union(optimized(l), optimized(r))
    case s: Select =>
      s.copy(
        join = s.join map {
          case j@Join(what: Statement, _, _, _) =>
            j.copy(what $ optimized)
          case j => j
        }
      ) $ groupByToDistinct
  }

  private def groupByToDistinct(select: Select): Select
  = if (select.groupBy.toSet == select.what.toSet && select.having.isEmpty)
    select.copy(groupBy = Nil, distinct = true)
  else
    select
}
