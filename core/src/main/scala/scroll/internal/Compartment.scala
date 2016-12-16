package scroll.internal

import java.lang.reflect.Method

import scroll.internal.errors.SCROLLErrors._
import scroll.internal.support._
import UnionTypes.RoleUnionTypes
import scroll.internal.graph.CachedScalaRoleGraph
import scroll.internal.util.ReflectiveHelper

import scala.util.{Failure, Success, Try}
import scala.annotation.tailrec
import scala.reflect.{ClassTag, classTag}

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
    with RoleUnionTypes {

  protected val plays = new CachedScalaRoleGraph()

  implicit def either2TorException[T](either: Either[_, T]): T = either.fold(
    l => {
      throw new RuntimeException(l.toString)
    }, r => {
      r
    })

  /**
    * Declaring a is-part-of relation between compartments.
    */
  def partOf(other: Compartment): Unit = {
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
  def notPartOf(other: Compartment): Unit = {
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
  def all[T: ClassTag](matcher: RoleQueryStrategy = MatchAny()): Seq[T] = {
    plays.allPlayers.filter(ReflectiveHelper.is[T]).map(_.asInstanceOf[T]).filter(a => {
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
  def all[T: ClassTag](matcher: T => Boolean): Seq[T] =
    plays.allPlayers.filter(ReflectiveHelper.is[T]).map(_.asInstanceOf[T]).filter(a => {
      getCoreFor(a) match {
        case p :: Nil => matcher(p.asInstanceOf[T])
        case Nil => false
        case l: Seq[Any] => l.forall(i => matcher(i.asInstanceOf[T]))
      }
    })

  private def safeReturn[T](seq: Seq[T], typeName: String): Either[TypeError, Seq[T]] = seq match {
    case Nil => Left(TypeNotFound(typeName))
    case s => Right(s)
  }

  /**
    * Query the role playing graph for all player instances that do conform to the given matcher and return the first found.
    *
    * @param matcher the matcher that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instance, that does conform to the given matcher or an appropriate error
    */
  def one[T: ClassTag](matcher: RoleQueryStrategy = MatchAny()): Either[TypeError, T] = safeReturn(all[T](matcher), classTag[T].toString).fold(
    l => {
      Left(l)
    }, r => {
      Right(r.head)
    })


  /**
    * Query the role playing graph for all player instances that do conform to the given function and return the first found.
    *
    * @param matcher the matching function that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instances, that do conform to the given matcher or an appropriate error
    */
  def one[T: ClassTag](matcher: T => Boolean): Either[TypeError, T] = safeReturn(all[T](matcher), classTag[T].toString).fold(
    l => {
      Left(l)
    }, r => {
      Right(r.head)
    })

  /**
    * Adds a play relation between core and role.
    *
    * @tparam C type of core
    * @tparam R type of role
    * @param core the core to add the given role at
    * @param role the role that should added to the given core
    */
  def addPlaysRelation[C <: AnyRef : ClassTag, R <: AnyRef : ClassTag](core: C, role: R): Unit = {
    require(null != core)
    require(null != role)
    validate(core, role)
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
  def removePlaysRelation[C <: AnyRef : ClassTag, R <: AnyRef : ClassTag](core: C, role: R): Unit = {
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
    * @param coreTo   the core the given role should be attached to
    * @param role     the role that should be transferred
    */
  def transferRole[F <: AnyRef : ClassTag, T <: AnyRef : ClassTag, R <: AnyRef : ClassTag](coreFrom: F, coreTo: T, role: R): Unit = {
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
      case cur: Any if plays.containsPlayer(cur) => plays.getPredecessors(cur).toList match {
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
  trait SCROLLDynamic extends Dynamic {
    /**
      * Allows to call a function with arguments.
      *
      * @param name          the function name
      * @param args          the arguments handed over to the given function
      * @param dispatchQuery the dispatch rules that should be applied
      * @tparam E return type
      * @tparam A argument type
      * @return the result of the function call or an appropriate error
      */
    def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E]

    /**
      * Allows to call a function with named arguments.
      *
      * @param name          the function name
      * @param args          tuple with the the name and argument handed over to the given function
      * @param dispatchQuery the dispatch rules that should be applied
      * @tparam E return type
      * @return the result of the function call or an appropriate error
      */
    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E]

    /**
      * Allows to read a field.
      *
      * @param name          of the field
      * @param dispatchQuery the dispatch rules that should be applied
      * @tparam E return type
      * @return the result of the field access or an appropriate error
      */
    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E]

    /**
      * Allows to write field updates.
      *
      * @param name          of the field
      * @param value         the new value to write
      * @param dispatchQuery the dispatch rules that should be applied
      */
    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit
  }

  trait Dispatchable {
    /**
      * For empty argument list dispatch.
      *
      * @param on the instance to dispatch the given method m on
      * @param m  the method to dispatch
      * @tparam E the return type of method m
      * @return the resulting return value of the method invocation or an appropriate error
      */
    def dispatch[E](on: Any, m: Method): Either[InvocationError, E]

    /**
      * For multi-argument dispatch.
      *
      * @param on   the instance to dispatch the given method m on
      * @param m    the method to dispatch
      * @param args the arguments to pass to method m
      * @tparam E the return type of method m
      * @tparam A the type of the argument values
      * @return the resulting return value of the method invocation or an appropriate error
      */
    def dispatch[E, A](on: Any, m: Method, args: Seq[A]): Either[InvocationError, E]
  }

  /**
    * Trait handling the actual dispatching of role methods.
    */
  trait SCROLLDispatch extends Dispatchable {
    override def dispatch[E](on: Any, m: Method): Either[InvocationError, E] = {
      require(null != on)
      require(null != m)
      Try(ReflectiveHelper.resultOf[E](on, m)) match {
        case Success(s) => Right(s)
        case Failure(_) => Left(IllegalRoleInvocationSingleDispatch(on.toString, m.getName))
      }
    }

    override def dispatch[E, A](on: Any, m: Method, args: Seq[A]): Either[InvocationError, E] = {
      require(null != on)
      require(null != m)
      require(null != args)
      Try(ReflectiveHelper.resultOf[E](on, m, args.map(_.asInstanceOf[Object]))) match {
        case Success(s) => Right(s)
        case Failure(_) => Left(IllegalRoleInvocationMultipleDispatch(on.toString, m.getName, args.toString()))
      }
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
  implicit class Player[T <: AnyRef : ClassTag](val wrapped: T) extends SCROLLDynamic with SCROLLDispatch {
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
      * @return the player of this player instance if this is a role, or this itself or an appropriate error
      */
    def player(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[TypeError, Any] = dispatchQuery.filter(getCoreFor(this)) match {
      case elem :: Nil => Right(elem)
      case l: Seq[T] => Right(l.head)
      case _ => Left(TypeNotFound(this.getClass.toString))
    }

    /**
      * Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def play[R <: AnyRef : ClassTag](role: R): Player[T] = {
      wrapped match {
        case p: Player[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: Any => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case _ => // do nothing
      }
      this
    }

    /**
      * Alias for [[Player.play]].
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def <+>[R <: AnyRef : ClassTag](role: R): Player[T] = play(role)

    /**
      * Adds a play relation between core and role but always returns the player instance.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def playing[R <: AnyRef : ClassTag](role: R): T = play(role).wrapped

    /**
      * Alias for [[Player.playing]].
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def <=>[R <: AnyRef : ClassTag](role: R): T = playing(role)

    /**
      * Removes the play relation between core and role.
      *
      * @param role the role that should be removed
      * @return this
      */
    def drop[R <: AnyRef : ClassTag](role: R): Player[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    /**
      * Alias for [[Player.drop]].
      *
      * @param role the role that should be removed
      * @return this
      */
    def <->[R <: AnyRef : ClassTag](role: R): Player[T] = drop(role)

    /**
      * Transfers a role to another player.
      *
      * @tparam R type of role
      * @param role the role to transfer
      */
    def transfer[R <: AnyRef : ClassTag](role: R) = new {
      def to[P <: AnyRef : ClassTag](player: P): Unit = {
        transferRole[T, P, R](wrapped, player, role)
      }
    }

    /**
      * Checks of this Player is playing a role of the given type.
      */
    def isPlaying[E: ClassTag]: Boolean = plays.getRoles(wrapped).exists(ReflectiveHelper.is[E])

    /**
      * Checks of this Player has an extension of the given type.
      * Alias for [[Player.isPlaying]].
      */
    def hasExtension[E: ClassTag]: Boolean = isPlaying[E]

    override def applyDynamic[E, A](name: String)(args: A*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core).toSeq)
      anys.foreach(r => {
        ReflectiveHelper.findMethod(r, name, args.toSeq).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })
      // otherwise give up
      Left(RoleNotFound(core.toString, name, args.toString()))
    }

    override def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    override def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core).toSeq)
      anys.find(ReflectiveHelper.hasMember(_, name)) match {
        case Some(r) => Right(ReflectiveHelper.propertyOf(r, name))
        case None => Left(RoleNotFound(core.toString, name, ""))
      }
    }

    override def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit = {
      val core = dispatchQuery.filter(getCoreFor(wrapped)).head
      val anys = dispatchQuery.filter(Seq(core, wrapped) ++ plays.getRoles(core).toSeq)
      anys.find(ReflectiveHelper.hasMember(_, name)) match {
        case Some(r) => ReflectiveHelper.setPropertyOf(r, name, value)
        case None => // do nothing
      }
    }

    override def equals(o: Any): Boolean = o match {
      case other: Player[_] => getCoreFor(wrapped) == getCoreFor(other.wrapped)
      case other: Any => getCoreFor(wrapped) match {
        case Nil => false
        case p :: Nil => p == other
        case _ => false
      }
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}
