package scroll.internal

import java.lang
import java.lang.reflect.Method

import scroll.internal.support._
import UnionTypes.RoleUnionTypes
import scroll.internal.graph.ScalaRoleGraph

import scala.annotation.tailrec
import scala.reflect.Manifest
import scala.reflect.runtime.universe._

/**
  * This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  *
  * ==Overview==
  * Roles are dependent on some sort of context. We call them compartments. A typical example of a compartment is a university,
  * which contains the roles Student and Teacher collaborating in Courses. Everything in SCROLL happens inside of Compartments
  * but roles (implemented as standard Scala classes) can be defined or imported from everywhere. Just mix in this Trait
  * into your own specific compartment class or create an anonymous instance.
  *
  * ==Example==
  * {{{
  * val player = new Player()
  * new Compartment {
  *   class RoleA
  *   class RoleB
  *
  *   player play new RoleA()
  *   player play new RoleB()
  *
  *   // call some behaviour
  * }
  * }}}
  */
trait Compartment
  extends RoleConstraints
  with RoleRestrictions
  with RoleGroups
  with Relationships
  with QueryStrategies
  with RoleUnionTypes
  with Coroutines {

  protected var plays = new ScalaRoleGraph()

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
  def all[T: Manifest](matcher: RoleQueryStrategy = MatchAny()): Seq[T] = {
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
        case l: Seq[Any] => l.forall(i => matcher(i.asInstanceOf[T]))
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
  def one[T: Manifest](matcher: RoleQueryStrategy = MatchAny()): T = safeReturn(all[T](matcher), manifest[T].toString()) match {
    case elem :: Nil => elem
    case l: Seq[T] => l.head
    case _ => throw new RuntimeException(s"Query for such a type unsuccessful.")
  }

  /**
    * Query the role playing graph for all player instances that do conform to the given function and return the first found.
    *
    * @param matcher the matching function that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instances, that do conform to the given matcher
    */
  def one[T: Manifest](matcher: T => Boolean): T = safeReturn(all[T](matcher), manifest[T].toString()) match {
    case elem :: Nil => elem
    case l: Seq[T] => l.head
    case _ => throw new RuntimeException(s"Query for such a type unsuccessful.")
  }

  /**
    * Adds a play relation between core and role.
    *
    * @tparam C type of core
    * @tparam R type of role
    * @param core the core to add the given role at
    * @param role the role that should added to the given core
    */
  def addPlaysRelation[C <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](core: C, role: R) {
    require(null != core)
    require(null != role)
    validate(core, weakTypeOf[R])
    plays.addBinding(core, role)
  }

  /**
    * Removes the play relation between core and role.
    *
    * @tparam C type of core
    * @tparam R type of role
    * @param core the core the given role should removed from
    * @param role the role that should removed from the given core
    */
  def removePlaysRelation[C <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](core: C, role: R) {
    require(null != core)
    require(null != role)
    plays.removeBinding(core, role)
  }

  /**
    * Transfers a role from one core to another.
    *
    * @tparam F type of core the given role should be removed from
    * @tparam T type of core the given role should be attached to
    * @tparam R type of role
    * @param coreFrom the core the given role should be removed from
    * @param coreTo the core the given role should be attached to
    * @param role the role that should be transferred
    */
  def transferRole[F <: AnyRef : WeakTypeTag, T <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](coreFrom: F, coreTo: T, role: R) {
    require(null != coreFrom)
    require(null != coreTo)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
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
        case l: List[Any] => l
      }
      case _ => Seq(role)
    }
  }

  /**
    * Generic Trait that enables dynamic invocation of role methods that are not natively available on the player object.
    */
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

  /**
    * Trait handling the actual dispatching of role methods.
    */
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
    * Explicit helper factory method for creating a new Player instance
    * without the need to relying on the implicit mechanics of Scala.
    *
    * @param obj the player or role that is wrapped into this dynamic player type
    * @return a new Player instance wrapping the given object
    */
  def newPlayer(obj: Object): Player[Object] = new Player(obj)

  /**
    * Implicit wrapper class to add basic functionality to roles and its players as unified types.
    *
    * @param wrapped the player or role that is wrapped into this dynamic type
    * @tparam T type of wrapped object
    */
  implicit class Player[T <: AnyRef : WeakTypeTag](val wrapped: T) extends DynamicType with DispatchType {
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
    def player(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Any = dispatchQuery.reorder(getCoreFor(this)) match {
      case elem :: Nil => elem
      case l: Seq[T] => l.head
      case _ => throw new RuntimeException(s"Query for such a player unsuccessful.")
    }

    /**
      * Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return this
      */
    def play[R <: AnyRef : WeakTypeTag](role: R): Player[T] = {
      wrapped match {
        case p: Player[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: Any => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case _ => throw new RuntimeException(s"'$wrapped' cannot play role '$role' because its neither of type 'Player' nor 'Any'!") // default case
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
    def playing[R <: AnyRef : WeakTypeTag](role: R): T = play(role).wrapped

    /**
      * Removes the play relation between core and role.
      *
      * @param role the role that should be removed
      * @return this
      */
    def drop[R <: AnyRef : WeakTypeTag](role: R): Player[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    /**
      * Transfers a role to another player.
      *
      * @tparam R type of role
      * @param role the role to transfer
      */
    def transfer[R <: AnyRef : WeakTypeTag](role: R) = new {
      def to[P <: AnyRef : WeakTypeTag](player: P) {
        transferRole[T, P, R](Player.this.wrapped, player, role)
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
      val s = new StringBuilder()
      fn.foreach(c => translationRules.get(c.toString) match {
        case Some(r) => s.append(r)
        case None => s.append(c)
      })
      s.toString()
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
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}
