package internal

import scala.reflect.runtime.universe._

object ReflectiveHelper {
  private def simpleClassName(s: String, on: String) = s.contains(on) match {
    case true => s.substring(s.lastIndexOf(on) + 1)
    case false => s
  }

  def typeSimpleClassName(t: Type): String = simpleClassName(t.toString, ".")

  def classSimpleClassName(t: Class[_]): String = t.toString.contains("$anon$") match {
    case true => "Anonymous class in " + classSimpleClassName(t.getEnclosingClass)
    case false => simpleClassName(t.toString, "$")
  }
}

trait ReflectiveHelper {

  implicit class Reflective(cur: Any) {
    private lazy val methods = cur.getClass.getDeclaredMethods
    private lazy val fields = cur.getClass.getDeclaredFields

    def hasAttribute(name: String): Boolean = fields.find(m => m.getName == name) match {
      case None => false
      case _ => true
    }

    def hasMethod(name: String): Boolean = methods.find(m => m.getName == name) match {
      case None => false
      case _ => true
    }

    def propertyOf[T](name: String): T = {
      val field = cur.getClass.getDeclaredField(name)
      field.setAccessible(true)
      field.get(cur).asInstanceOf[T]
    }

    def resultOf[T](name: String): T = {
      val method = cur.getClass.getDeclaredMethod(name)
      method.setAccessible(true)
      method.invoke(cur).asInstanceOf[T]
    }

    def is[T: WeakTypeTag]: Boolean = ReflectiveHelper.classSimpleClassName(cur.getClass) == ReflectiveHelper.typeSimpleClassName(weakTypeOf[T])
  }

}
