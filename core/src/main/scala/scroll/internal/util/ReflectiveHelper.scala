package scroll.internal.util

import scala.reflect.{ClassTag, classTag}

/**
  * Companion object for the Trait [[ReflectiveHelper]] containing some
  * useful functions for translating class and type names to Strings.
  */
object ReflectiveHelper {
  private def simpleClassName(s: String, on: String) = if (s.contains(on)) {
    s.substring(s.lastIndexOf(on) + 1)
  } else {
    s
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
    * Translates a Class or Type name to a String, i.e. removing anything before the last
    * occurrence of "<code>$</code>" or "<code>.</code>".
    *
    * @param t the Class or Type name as String
    * @return anything after the last occurrence of "<code>$</code>" or "<code>.</code>"
    */
  def simpleName(t: String): String = typeSimpleClassName(classSimpleClassName(t))

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

  import java.lang
  import java.lang.reflect.{Field, Method}

  /**
    * An implicit wrapper which provides helper functions
    * to access common tasks for working with reflections.
    *
    * @param cur the wrapped object to reflect on
    */
  implicit class Reflective(cur: Any) {
    private lazy val methods: Set[Method] = getAllMethods
    private lazy val fields: Set[Field] = getAllFields

    private def safeString(s: String): Unit = {
      require(null != s)
      require(!s.isEmpty)
    }

    private def safeFindField(name: String): Field = fields.find(_.getName == name) match {
      case Some(f) => f
      case None => throw new RuntimeException(s"Field '$name' not found!")
    }

    private def findMethods(name: String): Set[Method] = methods.filter(_.getName == name)

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

    private def matchMethod[A](m: Method, name: String, args: Seq[A]): Boolean = {
      lazy val matchName = m.getName == name
      lazy val matchParamCount = m.getParameterTypes.length == args.size
      lazy val matchArgTypes = args.zip(m.getParameterTypes).forall {
        case (arg, paramType: Class[_]) => paramType match {
          case lang.Boolean.TYPE => arg.isInstanceOf[Boolean]
          case lang.Character.TYPE => arg.isInstanceOf[Char]
          case lang.Short.TYPE => arg.isInstanceOf[Short]
          case lang.Integer.TYPE => arg.isInstanceOf[Integer]
          case lang.Long.TYPE => arg.isInstanceOf[Long]
          case lang.Float.TYPE => arg.isInstanceOf[Float]
          case lang.Double.TYPE => arg.isInstanceOf[Double]
          case lang.Byte.TYPE => arg.isInstanceOf[Byte]
          case _ => paramType.isAssignableFrom(arg.getClass)
        }
        case faultyArgs => throw new RuntimeException(s"Can not handle this arguments: '$faultyArgs'")
      }
      matchName && matchParamCount && matchArgTypes
    }

    /**
      * @return all methods/functions of the wrapped object as Set
      */
    def allMethods: Set[Method] = methods

    /**
      * Find a method of the wrapped object by its name and argument list given.
      *
      * @param name the name of the function/method of interest
      * @param args the args function/method of interest
      * @return Some(Method) if the wrapped object provides the function/method in question, None otherwise
      */
    def findMethod(name: String, args: Seq[Any]): Option[Method] = findMethods(name).find(matchMethod(_, name, args))

    /**
      * Checks if the wrapped object provides a member (field or function/method) with the given name.
      *
      * @param name the name of the member (field or function/method)  of interest
      * @return true if the wrapped object provides the given member, false otherwise
      */
    def hasMember(name: String): Boolean = {
      safeString(name)
      fields.exists(_.getName == name) || methods.exists(_.getName == name)
    }

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

    /**
      * Sets the field given as name to the provided value.
      *
      * @param name  the name of the field of interest
      * @param value the value to set for this field
      */
    def setPropertyOf(name: String, value: Any): Unit = {
      safeString(name)
      val field = safeFindField(name)
      field.setAccessible(true)
      field.set(cur, value)
    }

    /**
      * Returns the runtime result of type T of the given function by executing this function of the wrapped object.
      *
      * @param m the function of interest
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](m: Method): T = {
      m.setAccessible(true)
      m.invoke(cur).asInstanceOf[T]
    }

    /**
      * Returns the runtime result of type T of the given function and arguments by executing this function of the wrapped object.
      *
      * @param m    the function of interest
      * @param args the arguments of the function of interest
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](m: Method, args: Seq[Object]): T = {
      m.setAccessible(true)
      m.invoke(cur, args: _*).asInstanceOf[T]
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
      findMethods(name).toList match {
        case elem :: Nil =>
          elem.setAccessible(true)
          elem.invoke(cur).asInstanceOf[T]
        case list if list.nonEmpty =>
          val elem = list.head
          elem.setAccessible(true)
          elem.invoke(cur).asInstanceOf[T]
        case Nil =>
          throw new RuntimeException(s"Function with name '$name' not found!")
      }
    }

    /**
      * Checks if the wrapped object is of type T.
      *
      * @tparam T the type to check
      * @return true if the wrapped object is of type T, false otherwise
      */
    def is[T: ClassTag]: Boolean = ReflectiveHelper.simpleName(cur.getClass.toString) == ReflectiveHelper.simpleName(classTag[T].toString)
  }

}
