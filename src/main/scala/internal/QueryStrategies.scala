package internal

trait QueryStrategies extends ReflectiveHelper {

  implicit class RoleQueryStrategy(name: String) {
    def matches(on: Any): Boolean = true

    /**
     * Returns the value the queried attribute.
     *
     * @param value the name of the attribute that is queried
     * @tparam T its type
     * @return the value of the queried attribute
     */
    def ==#[T](value: T) = new WithProperty(name, value)

    /**
     * Returns the return value the queried function.
     *
     * @param value the name of the function that is queried
     * @tparam T its return type
     * @return the return value of the queried function
     */
    def ==>[T](value: T) = new WithResult(name, value)
  }

  case class *() extends RoleQueryStrategy("")

  case class WithProperty[T](name: String, value: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = on.propertyOf[T](name) == value
  }

  case class WithResult[T](name: String, result: T) extends RoleQueryStrategy(name) {
    override def matches(on: Any): Boolean = on.resultOf[T](name) == result
  }

}
