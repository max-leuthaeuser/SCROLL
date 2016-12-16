package scroll.internal.support

import scroll.internal.util.ReflectiveHelper

/**
  * Allows to write queries looking for the content of an attribute of the certain role playing
  * object or the return value of one of its functions.
  */
trait QueryStrategies {

  implicit class RoleQueryStrategy(name: String) {
    def matches(on: Any): Boolean = true

    /**
      * Returns the value the queried attribute.
      *
      * @param value the name of the attribute that is queried
      * @tparam T its type
      * @return the value of the queried attribute
      */
    def ==#[T](value: T): WithProperty[T] = WithProperty(name, value)

    /**
      * Returns the return value the queried function.
      *
      * @param value the name of the function that is queried
      * @tparam T its return type
      * @return the return value of the queried function
      */
    def ==>[T](value: T): WithResult[T] = WithResult(name, value)
  }

  case class MatchAny() extends RoleQueryStrategy("")

  case class WithProperty[T](name: String, value: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = ReflectiveHelper.propertyOf[T](on, name) == value
  }

  case class WithResult[T](name: String, result: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = ReflectiveHelper.resultOf[T](on, name) == result
  }

}
