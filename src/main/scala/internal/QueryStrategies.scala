package internal

trait QueryStrategies extends ReflectiveHelper {
  implicit class RoleQueryStrategy(name: String) {
    def matches(on: Any): Boolean = true

    def ==#[T](value: T) = new WithProperty(name, value)

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
