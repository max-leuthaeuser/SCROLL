package scroll.internal.util

import java.lang.reflect.Field
import java.lang.reflect.Method

import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.reflect.classTag

/**
  * Contains useful functions for translating class and type names to Strings
  * and provides helper functions to access common tasks for working with reflections.
  *
  * Querying methods and fields is cached using [[scroll.internal.util.Memoiser]].
  */
object ReflectiveHelper extends Memoiser {

  private[this] class MethodCache extends Memoised[Class[_], Set[Method]]

  private[this] class FieldCache extends Memoised[Class[_], Set[Field]]

  private[this] class SimpleTagNameCache extends Memoised[ClassTag[_], String]

  private[this] class SimpleClassNameCache extends Memoised[Class[_], String]

  private[this] lazy val methodCache = new MethodCache()
  private[this] lazy val fieldCache = new FieldCache()
  private[this] lazy val simpleClassNameCache = new SimpleClassNameCache()
  private[this] lazy val simpleTagNameCache = new SimpleTagNameCache()

  def addToMethodCache(c: Class[_]): Unit = methodCache.put(c, allMethods(c))

  def addToFieldCache(c: Class[_]): Unit = fieldCache.put(c, allFields(c))

  private[this] def simpleClassName(s: String, on: String) = if (s.contains(on)) {
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
  def hash(of: AnyRef): String = of.hashCode().toString

  /**
    * Compares two class names.
    *
    * @param mani the first class name derived from a class manifest (e.g., from classTag) as String
    * @param that the second class name already as instance of Any
    * @return true iff both names are the same, false otherwise
    */
  def isInstanceOf(mani: String, that: AnyRef): Boolean =
    simpleName(that.getClass.toString) == simpleName(mani)

  /**
    * Compares two class names.
    *
    * @param mani the first class name derived from a class manifest (e.g., from classTag) as String
    * @param that the second class name already as String
    * @return true iff both names are the same, false otherwise
    */
  def isInstanceOf(mani: String, that: String): Boolean =
    ReflectiveHelper.simpleName(that) == ReflectiveHelper.simpleName(mani)

  /**
    * Compares two interfaces given as Array of its Methods.
    *
    * @param roleInterface  Array of Methods from the first interface
    * @param restrInterface Array of Methods from the second interface
    * @return true iff all methods from the restrInterface can be found in roleInterface, false otherwise
    */
  def isSameInterface(roleInterface: Array[Method], restrInterface: Array[Method]): Boolean =
    restrInterface.forall(method => roleInterface.exists(method.equals))

  private[this] def safeString(s: String): Unit = {
    require(null != s)
    require(!s.isEmpty)
  }

  @tailrec
  private[this] def safeFindField(of: Class[_], name: String): Field = fieldCache.get(of) match {
    case Some(fields) => fields.find(_.getName == name) match {
      case Some(f) => f
      case None => throw new RuntimeException(s"Field '$name' not found on '$of'!")
    }
    case None =>
      val fields = allFields(of)
      fieldCache.put(of, fields)
      safeFindField(of, name)
  }

  @tailrec
  private[this] def findMethods(of: Class[_], name: String): Set[Method] = methodCache.get(of) match {
    case Some(l) =>
      l.filter(_.getName == name)
    case None =>
      val methods = allMethods(of)
      methodCache.put(of, methods)
      findMethods(of, name)
  }

  private[this] def allMethods(of: Class[_]): Set[Method] = {
    def getAccessibleMethods(c: Class[_]): Set[Method] = c match {
      case null => Set.empty
      case _ => c.getDeclaredMethods.toSet ++ getAccessibleMethods(c.getSuperclass)
    }

    getAccessibleMethods(of)
  }

  private[this] def allFields(of: Class[_]): Set[Field] = {
    def accessibleFields(c: Class[_]): Set[Field] = c match {
      case null => Set.empty
      case _ => c.getDeclaredFields.toSet ++ accessibleFields(c.getSuperclass)
    }

    accessibleFields(of)
  }

  private[this] def isSameMethodName(m: Method, name: String): Boolean = m.getName == name

  private[this] def isSameNumberOfParameters(m: Method, size: Int): Boolean = m.getParameterCount == size

  private[this] def isSameArgumentTypes[A](m: Method, args: Seq[A]): Boolean = args.zip(m.getParameterTypes).forall {
    case (arg, paramType: Class[_]) => paramType match {
      case java.lang.Boolean.TYPE => arg.isInstanceOf[Boolean]
      case java.lang.Character.TYPE => arg.isInstanceOf[Char]
      case java.lang.Short.TYPE => arg.isInstanceOf[Short]
      case java.lang.Integer.TYPE => arg.isInstanceOf[Integer]
      case java.lang.Long.TYPE => arg.isInstanceOf[Long]
      case java.lang.Float.TYPE => arg.isInstanceOf[Float]
      case java.lang.Double.TYPE => arg.isInstanceOf[Double]
      case java.lang.Byte.TYPE => arg.isInstanceOf[Byte]
      case _ => arg == null || paramType.isAssignableFrom(arg.getClass)
    }
    case null => throw new IllegalArgumentException(s"Can not handle these arguments: '$args'")
  }

  private[this] def matchMethod[A](m: Method, name: String, args: Seq[A]): Boolean =
    isSameMethodName(m, name) && isSameNumberOfParameters(m, args.size) && isSameArgumentTypes(m, args)

  /**
    * @return all methods/functions of the wrapped object as Set
    */
  def allMethods(of: AnyRef): Set[Method] = methodCache.get(of.getClass) match {
    case Some(methods) => methods
    case None =>
      val methods = allMethods(of.getClass)
      methodCache.put(of.getClass, methods)
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
  def findMethod(on: AnyRef, name: String, args: Seq[Any]): Option[Method] = findMethods(on.getClass, name).find(matchMethod(_, name, args))

  /**
    * Checks if the wrapped object provides a member (field or function/method) with the given name.
    *
    * @param on   the instance to search on
    * @param name the name of the member (field or function/method)  of interest
    * @return true if the wrapped object provides the given member, false otherwise
    */
  def hasMember(on: AnyRef, name: String): Boolean = {
    safeString(name)

    val fields = fieldCache.get(on.getClass) match {
      case Some(fs) => fs
      case None =>
        val fs = allFields(on.getClass)
        fieldCache.put(on.getClass, fs)
        fs
    }

    val methods = methodCache.get(on.getClass) match {
      case Some(ms) => ms
      case None =>
        val ms = allMethods(on.getClass)
        methodCache.put(on.getClass, ms)
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
  def propertyOf[T](on: AnyRef, name: String): T = {
    safeString(name)
    val field = safeFindField(on.getClass, name)
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
  def setPropertyOf(on: AnyRef, name: String, value: Any): Unit = {
    safeString(name)
    val field = safeFindField(on.getClass, name)
    field.setAccessible(true)
    field.set(on, value)
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
  def resultOf[T](on: AnyRef, m: Method, args: Seq[Object]): T = {
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
  def resultOf[T](on: AnyRef, name: String): T = {
    safeString(name)
    findMethods(on.getClass, name).toList match {
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
  def is[T <: AnyRef : ClassTag](on: AnyRef): Boolean =
    simpleClassNameCache.getAndPutWithDefault(on.getClass, ReflectiveHelper.simpleName(on.getClass.toString)) ==
      simpleTagNameCache.getAndPutWithDefault(classTag[T], ReflectiveHelper.simpleName(classTag[T].toString))
}


