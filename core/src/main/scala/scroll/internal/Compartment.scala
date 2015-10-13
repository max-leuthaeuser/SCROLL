package scroll.internal

import UnionTypes.RoleUnionTypes
import scala.annotation.tailrec
import java.lang.reflect.Method
import reflect.Manifest
import annotations.Role
import scroll.internal.graph.{RoleConstraintsGraph, ScalaRoleGraph}
import java.lang
import scala.reflect.runtime.universe._

trait Compartment extends QueryStrategies with RoleUnionTypes {

  private lazy val plays = new ScalaRoleGraph()
  private lazy val roleConstraints = new RoleConstraintsGraph(plays)
  private lazy val roleRestrictions = new RoleRestrictions()

  def RoleRestriction[A: Manifest, B](implicit tag: WeakTypeTag[B]) {
    roleRestrictions.addRestriction[A, B]
  }

  /**
   * Adds an role implication constraint between the given role types.
   * Interpretation: if a core object plays an instance of role type A
   * it also has to play an instance of role type B.
   *
   * @tparam A type of role A
   * @tparam B type of role B that should be played implicitly if A is played
   */
  def RoleImplication[A: Manifest, B: Manifest]() {
    roleConstraints.addImplication[A, B]()
  }

  /**
   * Adds an role equivalent constraint between the given role types.
   * Interpretation: if a core object plays an instance of role type A
   * it also has to play an instance of role type B and visa versa.
   *
   * @tparam A type of role A that should be played implicitly if B is played
   * @tparam B type of role B that should be played implicitly if A is played
   */
  def RoleEquivalence[A: Manifest, B: Manifest]() {
    roleConstraints.addEquivalence[A, B]()
  }

  /**
   * Adds an role prohibition constraint between the given role types.
   * Interpretation: if a core object plays an instance of role type A
   * it is not allowed to play B as well.
   *
   * @tparam A type of role A
   * @tparam B type of role B that is not allowed to be played if A is played already
   */
  def RoleProhibition[A: Manifest, B: Manifest]() {
    roleConstraints.addProhibition[A, B]()
  }

  /**
   * Wrapping function that checks all available role constraints for
   * all core objects and its roles after the given function was executed.
   *
   * @param func the function to execute and check role constraints afterwards
   * @return Success iff no role constraint is violated, Failure with a RuntimeException otherwise
   */
  def RoleConstraintsChecked(func: => Unit): Either[String, RuntimeException] = {
    func
    plays.allPlayers.foreach(p => {
      val roles = plays.getRoles(p).diff(Set(p))
      roles.foreach(r => roleConstraints.validate(p, r) match {
        case Right(e) => return Right(e)
        case _ =>
      })
    })
    Left("All role constraints hold.")
  }

  import Relationship._

  object Relationship {

    abstract class Multiplicity

    abstract class ExpMultiplicity extends Multiplicity

    case class Many() extends ExpMultiplicity

    case class ConcreteValue(v: Int) extends ExpMultiplicity {
      require(v >= 0)

      def To(t: ExpMultiplicity): Multiplicity = RangeMultiplicity(v, t)
    }

    implicit def intToConcreteValue(v: Int): ConcreteValue = new ConcreteValue(v)

    case class RangeMultiplicity(from: ConcreteValue, to: ExpMultiplicity) extends Multiplicity

    def apply(name: String) = new {
      def from[L: Manifest](leftMul: Multiplicity) = new {
        def to[R: Manifest](rightMul: Multiplicity): Relationship[L, R] = new Relationship(name, leftMul, rightMul)
      }
    }

  }

  class Relationship[L: Manifest, R: Manifest](name: String,
                                               var leftMul: Multiplicity,
                                               var rightMul: Multiplicity) {

    private def checkMul[T](m: Multiplicity, on: Seq[T]): Seq[T] = {
      m match {
        case Many() => assert(on.nonEmpty, s"With left multiplicity for '$name' of '*', the resulting role set should not be empty!")
        case ConcreteValue(v) => assert(on.size == v, s"With a concrete multiplicity for '$name' of '$v' the resulting role set should have the same size!")
        case RangeMultiplicity(f, t) => (f, t) match {
          case (ConcreteValue(v1), ConcreteValue(v2)) => assert(v1 <= on.size && on.size <= v2, s"With a multiplicity for '$name' from '$v1' to '$v2', the resulting role set size should be in between!")
          case (ConcreteValue(v), Many()) => assert(v <= on.size, s"With a multiplicity for '$name' from '$v' to '*', the resulting role set size should be in between!")
        }
      }
      on
    }

    def left(matcher: L => Boolean = _ => true): Seq[L] = checkMul(leftMul, all[L](matcher))

    def right(matcher: R => Boolean = _ => true): Seq[R] = checkMul(rightMul, all[R](matcher))

  }

  @deprecated("Since we want to apply role playing for legacy code, do not use this any more!", "0.4")
  private def isRole(value: Any): Boolean = {
    require(null != value)
    value.getClass.isAnnotationPresent(classOf[Role])
  }

  /**
   * Declaring a is-part-of relation between compartments.
   */
  def partOf(other: Compartment) {
    require(null != other)
    plays.merge(other.plays)
  }

  /**
   * Declaring a bidirectional is-part-of relation between compartment.
   */
  def union(other: Compartment): Compartment = {
    require(null != other)
    other.partOf(this)
    this.partOf(other)
    this
  }

  /**
   * Removing is-part-of relation between compartments.
   */
  def notPartOf(other: Compartment) {
    require(null != other)
    plays.detach(other.plays)
  }

  /**
   * Query the role playing graph for all player instances that do conform to the given matcher.
   *
   * @param matcher the matcher that should match the queried player instance in the role playing graph
   * @tparam T the type of the player instance to query for
   * @return all player instances as Seq, that do conform to the given matcher
   */
  def all[T: Manifest](matcher: RoleQueryStrategy = *()): Seq[T] = {
    plays.allPlayers.filter(_.is[T]).map(_.asInstanceOf[T]).filter(a => {
      getCoreFor(a) match {
        case p :: Nil => matcher.matches(p)
        case Nil => false
        case l => l.forall(matcher.matches)
      }
    })
  }

  /**
   * Query the role playing graph for all player instances that do conform to the given function.
   *
   * @param matcher the matching function that should match the queried player instance in the role playing graph
   * @tparam T the type of the player instance to query for
   * @return all player instances as Seq, that do conform to the given matcher
   */
  def all[T: Manifest](matcher: T => Boolean): Seq[T] =
    plays.allPlayers.filter(_.is[T]).map(_.asInstanceOf[T]).filter(a => {
      getCoreFor(a) match {
        case p :: Nil => matcher(p.asInstanceOf[T])
        case Nil => false
        case l => l.forall(i => matcher(i.asInstanceOf[T]))
      }
    })

  private def safeReturn[T](seq: Seq[T], typeName: String): Seq[T] = seq.isEmpty match {
    case true => throw new RuntimeException(s"No player with type '$typeName' found!")
    case false => seq
  }

  /**
   * Query the role playing graph for all player instances that do conform to the given matcher and return the first found.
   *
   * @param matcher the matcher that should match the queried player instance in the role playing graph
   * @tparam T the type of the player instance to query for
   * @return the first player instances, that do conform to the given matcher
   */
  def one[T: Manifest](matcher: RoleQueryStrategy = *()): T = safeReturn(all[T](matcher), manifest[T].toString()).head

  /**
   * Query the role playing graph for all player instances that do conform to the given function and return the first found.
   *
   * @param matcher the matching function that should match the queried player instance in the role playing graph
   * @tparam T the type of the player instance to query for
   * @return the first player instances, that do conform to the given matcher
   */
  def one[T: Manifest](matcher: T => Boolean): T = safeReturn(all[T](matcher), manifest[T].toString()).head

  /**
   * Adds a play relation between core and role.
   *
   * @tparam R type of role
   * @param core the core to add the given role at
   * @param role the role that should added to the given core
   */
  def addPlaysRelation[R](core: Any, role: R)(implicit tag: WeakTypeTag[R]) {
    require(null != core)
    require(null != role)
    //require(isRole(role), "Argument for adding a role must be a role (you maybe want to add the @Role annotation).")
    // check role restrictions first
    roleRestrictions.validate(core, tag.tpe)
    plays.addBinding(core, role)
  }

  /**
   * Removes the play relation between core and role.
   *
   * @param core the core the given role should removed from
   * @param role the role that should removed from the given core
   */
  def removePlaysRelation(core: Any, role: Any) {
    require(null != core)
    require(null != role)
    //require(isRole(role), "Argument for removing a role must be a role (you maybe want to add the @Role annotation).")
    plays.removeBinding(core, role)
  }

  /**
   * Transfers a role from one core to another.
   *
   * @tparam R type of role
   * @param coreFrom the core the given role should removed from
   * @param coreTo the core the given should attached to
   * @param role the role that should be transferred
   */
  def transferRole[R](coreFrom: Any, coreTo: Any, role: R)(implicit tag: WeakTypeTag[R]) {
    require(null != coreFrom)
    require(null != coreTo)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    //require(isRole(role), "Argument for transferring a role must be a role (you maybe want to add the @Role annotation).")
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  @tailrec
  private def getCoreFor(role: Any): Seq[Any] = {
    require(null != role)
    role match {
      case cur: Player[_] => getCoreFor(cur.wrapped)
      case cur: Any if plays.containsPlayer(cur) => plays.getPredecessors(cur) match {
        case p :: Nil => getCoreFor(p)
        case Nil => Seq(cur)
        case l => l
      }
      case _ => Seq(role)
    }
  }

  trait DynamicType extends Dynamic {
    /**
     * Allows to call a function with arguments.
     *
     * @param name the function name
     * @param args the arguments handed over to the given function
     * @param dispatchQuery the dispatch rules that should be applied
     * @tparam E return type
     * @tparam A argument type
     * @return the result of the function call
     */
    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E

    /**
     * Allows to call a function with named arguments.
     *
     * @param name the function name
     * @param args tuple with the the name and argument handed over to the given function
     * @param dispatchQuery the dispatch rules that should be applied
     * @tparam E return type
     * @return the result of the function call
     */
    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E

    /**
     * Allows to write field accessors.
     *
     * @param name of the field
     * @param dispatchQuery the dispatch rules that should be applied
     * @tparam E return type
     * @return the result of the field access
     */
    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E

    /**
     * Allows to write field updates.
     *
     * @param name of the field
     * @param value the new value to write
     * @param dispatchQuery the dispatch rules that should be applied
     */
    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty)
  }

  trait DispatchType {
    private def handleAccessibility(of: Method) {
      if (!of.isAccessible) of.setAccessible(true)
    }

    /**
     * For empty argument list dispatch.
     */
    def dispatch[E](on: Any, m: Method): E = {
      require(null != on)
      require(null != m)
      handleAccessibility(m)
      m.invoke(on, Array.empty[Object]: _*).asInstanceOf[E]
    }

    /**
     * For multi-argument dispatch.
     */
    def dispatch[E, A](on: Any, m: Method, args: Seq[A]): E = {
      require(null != on)
      require(null != m)
      require(null != args)
      val actualArgs = args.zip(m.getParameterTypes).map {
        case (arg: Player[_], tpe: Class[_]) =>
          plays.getRoles(arg.wrapped).find(_.getClass == tpe) match {
            case Some(curRole) => curRole
            case None => throw new RuntimeException(s"No role for type '$tpe' found.")
          }
        case (arg@unchecked, tpe: Class[_]) => arg
      }
      handleAccessibility(m)
      m.invoke(on, actualArgs.map(_.asInstanceOf[Object]): _*).asInstanceOf[E]
    }

  }

  /**
   * Implicit wrapper class to add basic functionality to roles and its players as unified types.
   *
   * @param wrapped the player or role that is wrapped into this dynamic type
   * @tparam T type of wrapped
   */
  implicit class Player[T](val wrapped: T) extends DynamicType with DispatchType {
    /**
     * Applies lifting to Player
     *
     * @return an lifted Player instance with the calling object as wrapped.
     */
    def unary_+ : Player[T] = this

    /**
     * Returns the player of this player instance if this is a role, or this itself.
     *
     * @param dispatchQuery provide this to sort the resulting instances if a role instance is played by multiple core objects
     * @return the player of this player instance if this is a role, or this itself.
     */
    def player(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Any =
      dispatchQuery.reorder(getCoreFor(this)).head

    /**
     * Adds a play relation between core and role.
     *
     * @tparam R type of role
     * @param role the role that should played
     * @return this
     */
    def play[R](role: R)(implicit tag: WeakTypeTag[R]): Player[T] = {
      wrapped match {
        case p: Player[_] => addPlaysRelation(p.wrapped, role)
        case p: Any => addPlaysRelation(p, role)
      }
      this
    }

    /**
     * Adds a play relation between core and role but always returns the player instance.
     *
     * @tparam R type of role
     * @param role the role that should played
     * @return the player instance
     */
    def playing[R](role: R): T = play(role).wrapped

    /**
     * Removes the play relation between core and role.
     *
     * @param role the role that should be removed
     * @return this
     */
    def drop(role: Any): Player[T] = {
      removePlaysRelation(wrapped, role)
      this
    }

    /**
     * Transfers a role to another player.
     *
     * @tparam R type of role
     * @param role the role to transfer
     */
    def transfer[R](role: R) = new {
      def to(player: Any) {
        transferRole(this, player, role)
      }
    }

    /**
     * Checks of this Player is playing a role of the given type.
     */
    def isPlaying[E: Manifest]: Boolean = plays.getRoles(wrapped).exists(_.is[E])

    private val translationRules = Map(
      "=" -> "$eq",
      ">" -> "$greater",
      "<" -> "$less",
      "+" -> "$plus",
      "-" -> "$minus",
      "*" -> "$times",
      "/" -> "$div",
      "!" -> "$bang",
      "@" -> "$at",
      "#" -> "$hash",
      "%" -> "$percent",
      "^" -> "$up",
      "&" -> "$amp",
      "~" -> "$tilde",
      "?" -> "$qmark",
      "|" -> "$bar",
      "\\" -> "$bslash",
      ":" -> "$colon")

    private def translateFunctionName(fn: String): String = {
      var s = ""
      fn.foreach(c => translationRules.get(c.toString) match {
        case Some(r) => s = s + r
        case None => s = s + c
      })
      s
    }

    private def noRoleException(name: String, args: Seq[Any], c: Any): RuntimeException =
      new RuntimeException(s"No role with '$name${args.mkString("(", ",", ")")}' found! (core: '${c.getClass}')")

    private def matchMethod[A](m: Method, name: String, args: Seq[A]): Boolean = {
      lazy val matchName = m.getName == name
      lazy val matchParamCount = m.getParameterTypes.length == args.size
      lazy val matchArgTypes = args.zip(m.getParameterTypes).forall {
        case (arg@unchecked, paramType: Class[_]) => paramType match {
          case lang.Boolean.TYPE => arg.isInstanceOf[Boolean]
          case lang.Character.TYPE => arg.isInstanceOf[Char]
          case lang.Short.TYPE => arg.isInstanceOf[Short]
          case lang.Integer.TYPE => arg.isInstanceOf[Integer]
          case lang.Long.TYPE => arg.isInstanceOf[Long]
          case lang.Float.TYPE => arg.isInstanceOf[Float]
          case lang.Double.TYPE => arg.isInstanceOf[Double]
          case lang.Byte.TYPE => arg.isInstanceOf[Byte]
          case _ if arg.getClass == this.getClass => getCoreFor(arg).flatMap(plays.getRoles).exists(_.getClass == paramType)

          case _ => paramType.isAssignableFrom(arg.getClass)
        }
      }
      matchName && matchParamCount && matchArgTypes
    }

    override def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val core = dispatchQuery.reorder(getCoreFor(wrapped)).head
      val anys = dispatchQuery.reorder(plays.getRoles(core).toSeq :+ wrapped :+ core)
      val functionName = translateFunctionName(name)
      anys.foreach(r => {
        r.allMethods.find(matchMethod(_, functionName, args.toSeq)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })
      // otherwise give up
      throw noRoleException(functionName, args.toSeq, core)
    }

    override def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    override def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): E = {
      val core = dispatchQuery.reorder(getCoreFor(wrapped)).head
      val anys = dispatchQuery.reorder(plays.getRoles(core).toSeq :+ wrapped :+ core)
      val attName = translateFunctionName(name)
      anys.find(_.hasAttribute(attName)) match {
        case Some(r) => r.propertyOf[E](attName)
        case None => throw noRoleException(attName, Seq.empty, wrapped)
      }
    }

    override def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty) {
      val core = dispatchQuery.reorder(getCoreFor(wrapped)).head
      val anys = dispatchQuery.reorder(plays.getRoles(core).toSeq :+ wrapped :+ core)
      val attName = translateFunctionName(name)
      anys.find(_.hasAttribute(attName)) match {
        case Some(r) => r.setPropertyOf(attName, value)
        case None => throw noRoleException(attName, Seq.empty, wrapped)
      }
    }

    override def equals(o: Any): Boolean = o match {
      case other: Player[_] => getCoreFor(this.wrapped) == getCoreFor(other.wrapped)
      case other: Any => getCoreFor(this.wrapped) match {
        case Nil => false
        case p :: Nil => p == other
        case _ => false
      }
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}
