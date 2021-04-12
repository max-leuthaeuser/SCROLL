package scroll.internal.support.impl

import scroll.internal.util.ReflectiveHelper

/** Allows to write queries looking for the content of an attribute of the certain role playing
  * object or the return value of one of its functions.
  */
object QueryStrategies {

  abstract class RoleQueryStrategy {
    def matches(on: AnyRef): Boolean
  }

  final case class MatchAny() extends RoleQueryStrategy {
    override def matches(on: AnyRef): Boolean = true
  }

  /** Query strategy using the value the queried attribute.
    *
    * @param name  the name of the attribute that is queried
    * @param value the value of the attribute that is queried
    * @tparam T its type
    */
  final case class WithProperty[T](name: String, value: T) extends RoleQueryStrategy {
    override def matches(on: AnyRef): Boolean = ReflectiveHelper.propertyOf[T](on, name) == value
  }

  /** Query strategy using the return value the queried function.
    *
    * @param name   the name of the function that is queried
    * @param result the return value of the function that is queried
    * @tparam T its type
    */
  final case class WithResult[T](name: String, result: T) extends RoleQueryStrategy {
    override def matches(on: AnyRef): Boolean = ReflectiveHelper.resultOf[T](on, name) == result
  }

}
