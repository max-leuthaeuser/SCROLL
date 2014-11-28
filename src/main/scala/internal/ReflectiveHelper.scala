package internal

import reflect.runtime.universe._

object ReflectiveHelper {
  def typeSimpleClassName(t: Type): String = t.toString.contains(".") match {
    case true => t.toString.substring(t.toString.lastIndexOf(".") + 1)
    case false => t.toString
  }
}

trait ReflectiveHelper {
  implicit class Reflective(cur: Any) {
    def hasAttribute(name: String): Boolean = cur.getClass.getDeclaredFields.find(m => m.getName == name) match {
      case None => false
      case _ => true
    }

    def hasMethod(name: String): Boolean = cur.getClass.getDeclaredMethods.find(m => m.getName == name) match {
      case None => false
      case _ => true
    }

    def property[T](name: String): T =
      {
        val field = cur.getClass.getDeclaredField(name)
        field.setAccessible(true)
        field.get(cur).asInstanceOf[T]
      }

    def is[T: WeakTypeTag]: Boolean = cur.getClass.getSimpleName == ReflectiveHelper.typeSimpleClassName(weakTypeOf[T])
  }
}
