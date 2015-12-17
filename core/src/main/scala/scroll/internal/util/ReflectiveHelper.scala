package scroll.internal.util

import java.lang.reflect.{Field, Method}

/**
  * Companion object for the Trait [[ReflectiveHelper]] containing some
  * useful functions for translating class and type names to Strings.
  */
object ReflectiveHelper {
  private def simpleClassName(s: String, on: String) = s.contains(on) match {
    case true => s.substring(s.lastIndexOf(on) + 1)
    case false => s
  }

  /**
    * Translates a Type name to a String, i.e. removing anything before the last
    * occurrence of "<code>.</code>".
    *
    * @param t the Type name as String
    * @return anything after the last occurrence of "<code>.</code>"
    */
  def typeSimpleClassName(t: String): String = simpleClassName(t, ".")

  /**
    * Translates a Class name to a String, i.e. removing anything before the last
    * occurrence of "<code>$</code>".
    *
    * @param t the Class name as String
    * @return anything after the last occurrence of "<code>$</code>"
    */
  def classSimpleClassName(t: String): String = simpleClassName(t, "$")

  /**
    * Returns the hash code of any object as String.
    *
    * @param of the object to get the hash code as String
    * @return the hash code of 'of' as String.
    */
  def hash(of: Any): String = of.hashCode().toString
}

/**
  * Trait containing an implicit wrapper which provides helper functions
  * to access common tasks for working with reflections.
  */
trait ReflectiveHelper {

  /**
    * An implicit wrapper which provides helper functions
    * to access common tasks for working with reflections.
    *
    * @param cur the wrapped object to reflect on
    */
  implicit class Reflective(cur: Any) {
    private lazy val methods: Set[Method] = getAllMethods
    private lazy val fields: Set[Field] = getAllFields

    private def safeString(s: String) {
      require(null != s)
      require(!s.isEmpty)
    }

    private def safeFindField(name: String): Field = fields.find(_.getName == name) match {
      case Some(f) => f
      case None => throw new RuntimeException(s"Field '$name' not found!")
    }

    private def safeFindMethod(name: String) = methods.find(_.getName == name) match {
      case Some(m) => m
      case None => throw new RuntimeException(s"Method '$name' not found!")
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

    /**
      * Checks if the wrapped object provides a field with the given name.
      *
      * @param name the name of the field of interest
      * @return true if the wrapped object provides the given field, false otherwise
      */
    def hasAttribute(name: String): Boolean = {
      safeString(name)
      fields.exists(_.getName == name)
    }

    /**
      * Checks if the wrapped object provides a method/function with the given name.
      *
      * @param name the name of the method/function of interest
      * @return true if the wrapped object provides the given method/function, false otherwise
      */
    def hasMethod(name: String): Boolean = {
      safeString(name)
      methods.exists(_.getName == name)
    }

    /**
      * @return all methods/functions of the wrapped object as Set
      */
    def allMethods: Set[Method] = methods

    /**
      * Returns the runtime content of type T of the field with the given name of the wrapped object.
      *
      * @param name the name of the field of interest
      * @tparam T the type of the field
      * @return the runtime content of type T of the field with the given name of the wrapped object
      */
    def propertyOf[T](name: String): T = {
      safeString(name)
      val field = safeFindField(name)
      field.setAccessible(true)
      field.get(cur).asInstanceOf[T]
    }

    def setPropertyOf(name: String, value: Any) {
      safeString(name)
      val field = safeFindField(name)
      field.setAccessible(true)
      field.set(cur, value)
    }

    /**
      * Returns the runtime result of type T of the function with the given name by executing this function of the wrapped object.
      *
      * @param name the name of the function of interest
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](name: String): T = {
      safeString(name)
      val method = safeFindMethod(name)
      method.setAccessible(true)
      method.invoke(cur).asInstanceOf[T]
    }

    /**
      * Checks if the wrapped object is of type T.
      *
      * @tparam T the type to check
      * @return true if the wrapped object is of type T, false otherwise
      */
    def is[T: Manifest]: Boolean =
      ReflectiveHelper.typeSimpleClassName(cur.getClass.toString) == ReflectiveHelper.typeSimpleClassName(manifest[T].toString())
  }

}
