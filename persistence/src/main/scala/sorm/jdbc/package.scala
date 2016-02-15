package sorm.jdbc

import java.sql.{Connection, PreparedStatement, ResultSet}

import org.joda.time._
import sorm.core.SormException

object `package` {

  implicit def connectionAdapter(x: Connection): JdbcConnection = new JdbcConnection(x)

  implicit def preparedStatementAdapter(x: PreparedStatement): PreparedStatementView = new PreparedStatementView(x)

  implicit def resultSetAdapter(x: ResultSet): ResultSetView = new ResultSetView(x)


  type JdbcType = Int

  object JdbcType {

    import java.sql.Types._

    /**
      * Shouldn't really be used
      */
    def apply(v: Any)
    = v match {
      case _: String => VARCHAR
      case _: BigDecimal => DECIMAL
      case _: Boolean => BIT
      case _: Byte => TINYINT
      case _: Short => SMALLINT
      case _: Int => INTEGER
      case _: Long => BIGINT
      case _: Float => REAL
      case _: Double => DOUBLE
      case _: LocalDate => DATE
      case _: LocalTime => TIME
      case _: DateTime => TIMESTAMP
      case null => NULL
      case _ => throw new SormException("Value of unsupported type `" + v.getClass + "`: " + v)
    }
  }

}