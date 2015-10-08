package scroll.internal

import java.lang.reflect.{Field, Method}

object ReflectiveHelper {
  private def simpleClassName(s: String, on: String) = s.contains(on) match {
    case true => s.substring(s.lastIndexOf(on) + 1)
    case false => s
  }

  def typeSimpleClassName(t: String): String = simpleClassName(t, ".")

  def classSimpleClassName(t: String): String = simpleClassName(t, "$")

  def hash(of: Any): String = of.hashCode().toString
}

trait ReflectiveHelper {

  implicit class Reflective(cur: Any) {
    private lazy val methods = getAllMethods
    private lazy val fields = getAllFields

    private def safeString(s: String) {
      require(null != s)
      require(!s.isEmpty)
    }

    private def getAllMethods: Set[Method] = {
      def getAccessibleMethods(c: Class[_]): Set[Method] = c match {
        case null => Set.empty
        case _ => c.getDeclaredMethods.toSet ++ getAccessibleMethods(c.getSuperclass)
      }
      getAccessibleMethods(cur.getClass)
    }

    private def getAllFields: Set[Field] = {
      def getAccessibleFields(c: Class[_]): Set[Field] = c match {
        case null => Set.empty
        case _ => c.getDeclaredFields.toSet ++ getAccessibleFields(c.getSuperclass)
      }
      getAccessibleFields(cur.getClass)
    }

    def hasAttribute(name: String): Boolean = {
      safeString(name)
      fields.exists(_.getName == name)
    }

    def hasMethod(name: String): Boolean = {
      safeString(name)
      methods.exists(_.getName == name)
    }

    def allMethods: Set[Method] = methods

    def propertyOf[T](name: String): T = {
      safeString(name)
      val field = fields.find(_.getName == name).get
      field.setAccessible(true)
      field.get(cur).asInstanceOf[T]
    }

    def setPropertyOf(name: String, value: Any) {
      safeString(name)
      val field = fields.find(_.getName == name).get
      field.setAccessible(true)
      field.set(cur, value)
    }

    def resultOf[T](name: String): T = {
      safeString(name)
      val method = methods.find(_.getName == name).get
      method.setAccessible(true)
      method.invoke(cur).asInstanceOf[T]
    }

    def is[T: Manifest]: Boolean =
      ReflectiveHelper.classSimpleClassName(cur.getClass.toString) == ReflectiveHelper.classSimpleClassName(manifest[T].toString())
  }

}
