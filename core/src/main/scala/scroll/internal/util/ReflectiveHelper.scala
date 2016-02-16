package scroll.internal.util

import scala.reflect.runtime.universe._

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
  private lazy val mirror = runtimeMirror(getClass.getClassLoader)

  /**
    * An implicit wrapper which provides helper functions
    * to access common tasks for working with reflections.
    *
    * @param cur the wrapped object to reflect on
    */
  implicit class Reflective(cur: Any) {
    private lazy val instanceMirror = mirror.reflect(cur)
    private lazy val members: Set[Symbol] = allMembersSet

    private def safeString(s: String): Unit = {
      require(null != s)
      require(!s.isEmpty)
    }

    private def getMember(name: String): Symbol = members.find(_.name.encodedName.toString == name) match {
      case Some(m) => m
      case None => throw new RuntimeException(s"Member with '$name' not found!")
    }

    private def getMembers(name: String): Set[Symbol] = members.filter(_.name.encodedName.toString == name)

    private def allMembersSet: Set[Symbol] = instanceMirror.symbol.typeSignature.members.toSet

    def getType: Type = instanceMirror.symbol.toType

    def allMembers: Set[Symbol] = members

    private def matchMethod[A](m: Symbol, name: String, args: Seq[A]): Boolean = {
      lazy val matchName = m.name.encodedName.toString == name
      lazy val params = m.asMethod.paramLists.flatten.map(_.typeSignature)
      lazy val matchParamCount = params.length == args.size
      lazy val matchArgTypes = args.zip(params).forall {
        case (a, p) =>
          p match {
            case p: Type if p =:= typeTag[Boolean].tpe => a.isInstanceOf[java.lang.Boolean]
            case p: Type if p =:= typeTag[Int].tpe => a.isInstanceOf[java.lang.Integer]
            case p: Type if p =:= typeTag[Double].tpe => a.isInstanceOf[java.lang.Double]
            case p: Type if p =:= typeTag[Float].tpe => a.isInstanceOf[java.lang.Float]
            case p: Type if p =:= typeTag[Long].tpe => a.isInstanceOf[java.lang.Long]
            case p: Type if p =:= typeTag[Short].tpe => a.isInstanceOf[java.lang.Short]
            case p: Type if p =:= typeTag[Byte].tpe => a.isInstanceOf[java.lang.Byte]
            case p: Type if p =:= typeTag[Char].tpe => a.isInstanceOf[java.lang.Character]
            case _ => a.getType =:= p || a.getType <:< p
          }
      }
      matchName && matchParamCount && matchArgTypes
    }

    /**
      * Find a method of the wrapped object by its name and argument list given.
      *
      * @param name the name of the function/method of interest
      * @param args the args function/method of interest
      * @return Some(Symbol) if the wrapped object provides the function/method in question, None otherwise
      */
    def findMethod(name: String, args: Seq[Any]): Option[Symbol] = getMembers(name).find(matchMethod(_, name, args))

    /**
      * Checks if the wrapped object provides a member (field or function/method) with the given name.
      *
      * @param name the name of the member (field or function/method)  of interest
      * @return true if the wrapped object provides the given field, false otherwise
      */
    def hasMember(name: String): Boolean = {
      safeString(name)
      members.exists(_.name.encodedName.toString == name)
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
      instanceMirror.reflectField(getMember(name).asTerm).get.asInstanceOf[T]
    }

    def setPropertyOf(name: String, value: Any): Unit = {
      safeString(name)
      instanceMirror.reflectField(getMember(name).asTerm).set(value)
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
      instanceMirror.reflectMethod(getMember(name).asMethod).apply().asInstanceOf[T]
    }

    /**
      * Returns the runtime result of type T of the function given by executing this function of the wrapped object.
      *
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](m: Symbol): T = instanceMirror.reflectMethod(m.asMethod).apply().asInstanceOf[T]

    /**
      * Returns the runtime result of type T of the function with the given name and arguments by executing this function of the wrapped object.
      *
      * @param name the name of the function of interest
      * @param args the arguments of the function of interest
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](name: String, args: Seq[Any]): T = {
      safeString(name)
      instanceMirror.reflectMethod(getMember(name).asMethod).apply(args: _*).asInstanceOf[T]
    }

    /**
      * Returns the runtime result of type T of the given function and arguments by executing this function of the wrapped object.
      *
      * @param m    the function of interest
      * @param args the arguments of the function of interest
      * @tparam T the return type of the function
      * @return the runtime result of type T of the function with the given name by executing this function of the wrapped object
      */
    def resultOf[T](m: Symbol, args: Seq[Any]): T =
      instanceMirror.reflectMethod(m.asMethod).apply(args: _*).asInstanceOf[T]

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
