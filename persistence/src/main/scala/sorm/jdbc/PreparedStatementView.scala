package sorm.jdbc

import java.sql.PreparedStatement

import org.joda.time._
import sorm.joda.Extensions._

class PreparedStatementView
(s: PreparedStatement) {
  /**
    * @see <a href=http://docstore.mik.ua/orelly/java-ent/servlet/ch09_02.htm#ch09-22421>jdbc table
    */
  def set
  (i: Int,
   v: Any): Unit = {
    v match {
      case v: Boolean => s.setBoolean(i, v)
      case v: String => s.setString(i, v)
      case v: Byte => s.setByte(i, v)
      case v: Short => s.setShort(i, v)
      case v: Int => s.setInt(i, v)
      case v: Long => s.setLong(i, v)
      case v: Float => s.setFloat(i, v)
      case v: Double => s.setDouble(i, v)
      case v: BigDecimal => s.setBigDecimal(i, v.bigDecimal)
      case v: LocalDate => s.setDate(i, v.toJava)
      case v: LocalTime => s.setTime(i, v.toJava)
      case v: DateTime => s.setTimestamp(i, v.toJava)
      case null => s.setNull(i, java.sql.Types.NULL)
      case _ => ???
    }
  }

  def set
  (i: Int,
   v: JdbcValue): Unit = {
    set(i, v.value)
  }
}
