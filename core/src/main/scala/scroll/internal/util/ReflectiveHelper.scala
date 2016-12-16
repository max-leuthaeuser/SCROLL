package scroll.internal.util

import scala.annotation.tailrec
import scala.reflect.{ClassTag, classTag}

/**
  * Contains useful functions for translating class and type names to Strings
  * and provides helper functions to access common tasks for working with reflections.
  *
  * Querying methods and fields is cached using [[scroll.internal.util.Memoiser]].
  */
object ReflectiveHelper extends Memoiser {

  import java.lang
  import java.lang.reflect.{Field, Method}

  private class MethodCache extends Memoised[Any, Set[Method]]

  private class FieldCache extends Memoised[Any, Set[Field]]

  private lazy val methodCache = new MethodCache()
  private lazy val fieldCache = new FieldCache()

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


  private def safeString(s: String): Unit = {
    require(null != s)
    require(!s.isEmpty)
  }

  @tailrec
  private def safeFindField(of: Any, name: String): Field = fieldCache.get(of) match {
    case Some(fields) => fields.find(_.getName == name) match {
      case Some(f) => f
      case None => throw new RuntimeException(s"Field '$name' not found on '$of'!")
    }
    case None =>
      val fields = getAllFields(of)
      fieldCache.put(of, fields)
      safeFindField(of, name)
  }

  @tailrec
  private def findMethods(of: Any, name: String): Set[Method] = methodCache.get(of) match {
    case Some(l) =>
      l.filter(_.getName == name)
    case None =>
      val methods = getAllMethods(of)
      methodCache.put(of, methods)
      findMethods(of, name)
  }

  private def getAllMethods(of: Any): Set[Method] = {
    def getAccessibleMethods(c: Class[_]): Set[Method] = c match {
      case null => Set.empty
      case _ => c.getDeclaredMethods.toSet ++ getAccessibleMethods(c.getSuperclass)
    }

    getAccessibleMethods(of.getClass)
  }

  private def getAllFields(of: Any): Set[Field] = {
    def getAccessibleFields(c: Class[_]): Set[Field] = c match {
      case null => Set.empty
      case _ => c.getDeclaredFields.toSet ++ getAccessibleFields(c.getSuperclass)
    }

    getAccessibleFields(of.getClass)
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
  def allMethods(of: Any): Set[Method] = methodCache.get(of) match {
    case Some(methods) => methods
    case None =>
      val methods = getAllMethods(of)
      methodCache.put(of, methods)
      methods
  }

  /**
    * Find a method of the wrapped object by its name and argument list given.
    *
    * @param on   the instance to search on
    * @param name the name of the function/method of interest
    * @param args the args function/method of interest
    * @return Some(Method) if the wrapped object provides the function/method in question, None otherwise
    */
  def findMethod(on: Any, name: String, args: Seq[Any]): Option[Method] = findMethods(on, name).find(matchMethod(_, name, args))

  /**
    * Checks if the wrapped object provides a member (field or function/method) with the given name.
    *
    * @param on   the instance to search on
    * @param name the name of the member (field or function/method)  of interest
    * @return true if the wrapped object provides the given member, false otherwise
    */
  def hasMember(on: Any, name: String): Boolean = {
    safeString(name)

    val fields = fieldCache.get(on) match {
      case Some(fs) => fs
      case None =>
        val fs = getAllFields(on)
        fieldCache.put(on, fs)
        fs
    }

    val methods = methodCache.get(on) match {
      case Some(ms) => ms
      case None =>
        val ms = getAllMethods(on)
        methodCache.put(on, ms)
        ms
    }

    fields.exists(_.getName == name) || methods.exists(_.getName == name)
  }

  /**
    * Returns the runtime content of type T of the field with the given name of the wrapped object.
    *
    * @param on   the instance to search on
    * @param name the name of the field of interest
    * @tparam T the type of the field
    * @return the runtime content of type T of the field with the given name of the wrapped object
    */
  def propertyOf[T](on: Any, name: String): T = {
    safeString(name)
    val field = safeFindField(on, name)
    field.setAccessible(true)
    field.get(on).asInstanceOf[T]
  }

  /**
    * Sets the field given as name to the provided value.
    *
    * @param on    the instance to search on
    * @param name  the name of the field of interest
    * @param value the value to set for this field
    */
  def setPropertyOf(on: Any, name: String, value: Any): Unit = {
    safeString(name)
    val field = safeFindField(on, name)
    field.setAccessible(true)
    field.set(on, value)
  }

  /**
    * Returns the runtime result of type T of the given function by executing this function of the wrapped object.
    *
    * @param on the instance to search on
    * @param m  the function of interest
    * @tparam T the return type of the function
    * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
    */
  def resultOf[T](on: Any, m: Method): T = {
    m.setAccessible(true)
    m.invoke(on).asInstanceOf[T]
  }

  /**
    * Returns the runtime result of type T of the given function and arguments by executing this function of the wrapped object.
    *
    * @param on   the instance to search on
    * @param m    the function of interest
    * @param args the arguments of the function of interest
    * @tparam T the return type of the function
    * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
    */
  def resultOf[T](on: Any, m: Method, args: Seq[Object]): T = {
    m.setAccessible(true)
    m.invoke(on, args: _*).asInstanceOf[T]
  }

  /**
    * Returns the runtime result of type T of the function with the given name by executing this function of the wrapped object.
    *
    * @param on   the instance to search on
    * @param name the name of the function of interest
    * @tparam T the return type of the function
    * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
    */
  def resultOf[T](on: Any, name: String): T = {
    safeString(name)
    findMethods(on, name).toList match {
      case elem :: Nil =>
        elem.setAccessible(true)
        elem.invoke(on).asInstanceOf[T]
      case list if list.nonEmpty =>
        val elem = list.head
        elem.setAccessible(true)
        elem.invoke(on).asInstanceOf[T]
      case Nil =>
        throw new RuntimeException(s"Function with name '$name' not found on '$on'!")
    }
  }

  /**
    * Checks if the wrapped object is of type T.
    *
    * @param on the instance to search on
    * @tparam T the type to check
    * @return true if the wrapped object is of type T, false otherwise
    */
  def is[T: ClassTag](on: Any): Boolean = ReflectiveHelper.simpleName(on.getClass.toString) == ReflectiveHelper.simpleName(classTag[T].toString)
}


