package scroll.internal.util

import java.lang.reflect.Field
import java.lang.reflect.Method
import scala.collection.immutable.ArraySeq
import scala.reflect.ClassTag
import scala.reflect.classTag
import scala.util.Try

/** Contains useful functions for translating class and type names to Strings and provides helper functions to access
  * common tasks for working with reflections.
  *
  * Querying methods and fields is cached.
  */
object ReflectiveHelper {

  import Memoiser._

  private lazy val methodCache =
    buildCache[Class[?], Seq[Method]](allMethods)

  private lazy val methodsByNameCache =
    buildCache[(Class[?], String), Seq[Method]]((t: (Class[?], String)) => cachedFindMethods(t._1, t._2))

  private lazy val methodMatchCache =
    buildCache[(Class[?], String, Seq[Any]), Option[Method]]((t: (Class[?], String, Seq[Any])) =>
      cachedFindMethod(t._1, t._2, t._3)
    )

  private lazy val fieldCache =
    buildCache[Class[?], Seq[Field]]((c: Class[?]) => allFields(c))

  private lazy val fieldByNameCache =
    buildCache[(Class[?], String), Field]((t: (Class[?], String)) => cachedFindField(t._1, t._2))

  private lazy val classNameCache =
    buildCache[String, String](cachedSimpleName)

  private lazy val hasMemberCache =
    buildCache[(Class[?], String), java.lang.Boolean]((t: (Class[?], String)) => cachedHasMember(t._1, t._2))

  def addToMethodCache(c: Class[?]): Unit = methodCache.put(c, allMethods(c))

  def addToFieldCache(c: Class[?]): Unit = fieldCache.put(c, allFields(c))

  private def simpleClassName(s: String, on: String) =
    if (s.contains(on)) {
      s.substring(s.lastIndexOf(on) + 1)
    } else {
      s
    }

  private def cachedSimpleName(t: String): String =
    simpleClassName(simpleClassName(t, "."), "$")

  /** Translates a Class or Type name to a String, i.e. removing anything before the last occurrence of "<code>$</code>"
    * or "<code>.</code>".
    *
    * @param t
    *   the Class or Type name as String
    * @return
    *   anything after the last occurrence of "<code>$</code>" or "<code>.</code>"
    */
  def simpleName(t: String): String = classNameCache.get(t)

  /** Compares two class names.
    *
    * @param mani
    *   the first class name derived from a class manifest (e.g., from classTag) as String
    * @param that
    *   the second class name already as instance of Any
    * @return
    *   true iff both names are the same, false otherwise
    */
  def isInstanceOf(mani: String, that: AnyRef): Boolean =
    simpleName(that.getClass.toString) == simpleName(mani)

  /** Compares two class names.
    *
    * @param mani
    *   the first class name derived from a class manifest (e.g., from classTag) as String
    * @param that
    *   the second class name already as String
    * @return
    *   true iff both names are the same, false otherwise
    */
  def isInstanceOf(mani: String, that: String): Boolean = simpleName(that) == simpleName(mani)

  /** Compares two interfaces given as Array of its Methods.
    *
    * @param roleInterface
    *   Array of Methods from the first interface
    * @param restrInterface
    *   Array of Methods from the second interface
    * @return
    *   true iff all methods from the restrInterface can be found in roleInterface, false otherwise
    */
  def isSameInterface(roleInterface: Array[Method], restrInterface: Array[Method]): Boolean =
    restrInterface.forall(method => roleInterface.exists(method.equals))

  private def cachedFindField(of: Class[?], name: String): Field =
    fieldCache
      .get(of)
      .find(_.getName == name)
      .getOrElse {
        throw new RuntimeException(s"Field '$name' not found on '$of'!")
      }

  private def findField(of: Class[?], name: String): Field = fieldByNameCache.get((of, name))

  private def cachedFindMethods(of: Class[?], name: String): Seq[Method] =
    methodCache.get(of).filter(_.getName == name)

  private def findMethods(of: Class[?], name: String): Seq[Method] =
    methodsByNameCache.get((of, name))

  private def allMethods(of: Class[?]): Seq[Method] = {
    def getAccessibleMethods(c: Class[?]): Seq[Method] =
      c match {
        case null => Seq.empty[Method]
        case _    =>
          ArraySeq
            .unsafeWrapArray(c.getDeclaredMethods)
            .concat(getAccessibleMethods(c.getSuperclass))
      }

    getAccessibleMethods(of).map { m =>
      Try(m.setAccessible(true)); m
    }
  }

  private def allFields(of: Class[?]): Seq[Field] = {
    def accessibleFields(c: Class[?]): Seq[Field] =
      c match {
        case null => Seq.empty[Field]
        case _    =>
          ArraySeq.unsafeWrapArray(c.getDeclaredFields).concat(accessibleFields(c.getSuperclass))
      }

    accessibleFields(of).map { f =>
      Try(f.setAccessible(true)); f
    }
  }

  private def isSameNumberOfParameters(m: Method, size: Int): Boolean =
    m.getParameterCount == size

  private def isSameArgumentTypes(m: Method, args: Seq[Any]): Boolean =
    args.zip(m.getParameterTypes).forall { case (arg, paramType) =>
      paramType match {
        case java.lang.Boolean.TYPE   => arg.isInstanceOf[Boolean]
        case java.lang.Character.TYPE => arg.isInstanceOf[Char]
        case java.lang.Short.TYPE     => arg.isInstanceOf[Short]
        case java.lang.Integer.TYPE   => arg.isInstanceOf[Integer]
        case java.lang.Long.TYPE      => arg.isInstanceOf[Long]
        case java.lang.Float.TYPE     => arg.isInstanceOf[Float]
        case java.lang.Double.TYPE    => arg.isInstanceOf[Double]
        case java.lang.Byte.TYPE      => arg.isInstanceOf[Byte]
        case _                        => arg == null || paramType.isAssignableFrom(arg.getClass)
      }
    }

  private def matchMethod(m: Method, args: Seq[Any]): Boolean =
    isSameNumberOfParameters(m, args.size) && isSameArgumentTypes(m, args)

  private def cachedFindMethod(on: Class[?], name: String, args: Seq[Any]): Option[Method] =
    findMethods(on, name).find(matchMethod(_, args))

  /** Find a method of the wrapped object by its name and argument list given.
    *
    * @param on
    *   the instance to search on
    * @param name
    *   the name of the function/method of interest
    * @param args
    *   the args function/method of interest
    * @return
    *   Some(Method) if the wrapped object provides the function/method in question, None otherwise
    */
  def findMethod(on: AnyRef, name: String, args: Seq[Any]): Option[Method] =
    methodMatchCache.get((on.getClass, name, args))

  private def cachedHasMember(on: Class[?], name: String): java.lang.Boolean = {
    lazy val fields  = fieldCache.get(on)
    lazy val methods = methodCache.get(on)
    fields.exists(_.getName == name) || methods.exists(_.getName == name)
  }

  /** Checks if the wrapped object provides a member (field or function/method) with the given name.
    *
    * @param on
    *   the instance to search on
    * @param name
    *   the name of the member (field or function/method) of interest
    * @return
    *   true if the wrapped object provides the given member, false otherwise
    */
  def hasMember(on: AnyRef, name: String): Boolean = hasMemberCache.get((on.getClass, name))

  /** Returns the runtime content of type T of the field with the given name of the wrapped object.
    *
    * @param on
    *   the instance to search on
    * @param name
    *   the name of the field of interest
    * @tparam T
    *   the type of the field
    * @return
    *   the runtime content of type T of the field with the given name of the wrapped object
    */
  def propertyOf[T](on: AnyRef, name: String): T =
    findField(on.getClass, name).get(on).asInstanceOf[T]

  /** Sets the field given as name to the provided value.
    *
    * @param on
    *   the instance to search on
    * @param name
    *   the name of the field of interest
    * @param value
    *   the value to set for this field
    */
  def setPropertyOf(on: AnyRef, name: String, value: Any): Unit =
    findField(on.getClass, name).set(on, value)

  /** Returns the runtime result of type T of the given function and arguments by executing this function of the wrapped
    * object.
    *
    * @param on
    *   the instance to search on
    * @param m
    *   the function of interest
    * @param args
    *   the arguments of the function of interest
    * @tparam T
    *   the return type of the function
    * @return
    *   the runtime result of type T of the function with the given name by executing this function of the wrapped
    *   object
    */
  def resultOf[T](on: AnyRef, m: Method, args: Seq[Any]): T = m.invoke(on, args*).asInstanceOf[T]

  /** Returns the runtime result of type T of the function with the given name by executing this function of the wrapped
    * object.
    *
    * @param on
    *   the instance to search on
    * @param name
    *   the name of the function of interest
    * @tparam T
    *   the return type of the function
    * @return
    *   the runtime result of type T of the function with the given name by executing this function of the wrapped
    *   object
    */
  def resultOf[T](on: AnyRef, name: String): T =
    findMethods(on.getClass, name) match {
      case elem +: _ =>
        elem.invoke(on).asInstanceOf[T]
      case Nil =>
        throw new RuntimeException(s"Function with name '$name' not found on '$on'!")
    }

  /** Checks if the wrapped object is of type T.
    *
    * @param on
    *   the instance to search on
    * @tparam T
    *   the type to check
    * @return
    *   true if the wrapped object is of type T, false otherwise
    */
  def is[T <: AnyRef: ClassTag](on: AnyRef): Boolean =
    simpleName(on.getClass.toString) == simpleName(classTag[T].toString)

}
