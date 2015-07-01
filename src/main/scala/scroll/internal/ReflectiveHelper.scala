package scroll.internal

object ReflectiveHelper {
  private def simpleClassName(s: String, on: String) = s.contains(on) match {
    case true => s.substring(s.lastIndexOf(on) + 1)
    case false => s
  }

  def typeSimpleClassName(t: String): String = simpleClassName(t, ".")

  def classSimpleClassName(t: String): String = simpleClassName(t, "$")
}

trait ReflectiveHelper {

  implicit class Reflective(cur: Any) {
    private lazy val methods = cur.getClass.getDeclaredMethods
    private lazy val fields = cur.getClass.getDeclaredFields

    private def safeString(s: String) {
      require(null != s)
      require(!s.isEmpty)
    }

    def hasAttribute(name: String): Boolean = {
      safeString(name)
      fields.exists(_.getName == name)
    }

    def hasMethod(name: String): Boolean = {
      safeString(name)
      methods.exists(_.getName == name)
    }

    def propertyOf[T](name: String): T = {
      safeString(name)
      val field = cur.getClass.getDeclaredField(name)
      field.setAccessible(true)
      field.get(cur).asInstanceOf[T]
    }

    def setPropertyOf(name: String, value: Any) {
      safeString(name)
      val field = cur.getClass.getDeclaredField(name)
      field.setAccessible(true)
      field.set(cur, value)
    }

    def resultOf[T](name: String): T = {
      safeString(name)
      val method = cur.getClass.getDeclaredMethod(name)
      method.setAccessible(true)
      method.invoke(cur).asInstanceOf[T]
    }

    def is[T: Manifest]: Boolean =
      ReflectiveHelper.classSimpleClassName(cur.getClass.toString) == ReflectiveHelper.classSimpleClassName(manifest[T].toString())
  }

}
