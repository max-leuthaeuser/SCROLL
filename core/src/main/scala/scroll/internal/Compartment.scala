package scroll.internal

import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.SCROLLError
import scroll.internal.errors.SCROLLErrors.TypeError
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.internal.graph.ScalaRoleGraph
import scroll.internal.graph.ScalaRoleGraphBuilder
import scroll.internal.support.DispatchQuery
import scroll.internal.support.QueryStrategies
import scroll.internal.support.Relationships
import scroll.internal.support.RoleConstraints
import scroll.internal.support.RoleGroups
import scroll.internal.support.RoleRestrictions
import scroll.internal.support.UnionTypes.RoleUnionTypes
import scroll.internal.util.ReflectiveHelper

import scala.annotation.tailrec
import scala.reflect.ClassTag
import scala.reflect.classTag

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

  private[internal] val plays: ScalaRoleGraph = ScalaRoleGraphBuilder.build

  implicit def either2TorException[T](either: Either[_, T]): T = either.fold(
    l => {
      throw new RuntimeException(l.toString)
    }, r => {
      r
    })

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
    * Declaring a is-part-of relation between compartments.
    */
  def partOf(other: Compartment): Unit = {
    require(null != other)
    val _ = plays.addPart(other.plays)
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
  def all[T <: AnyRef : ClassTag](matcher: RoleQueryStrategy = MatchAny()): Seq[T] = {
    plays.allPlayers.filter(ReflectiveHelper.is[T]).map(_.asInstanceOf[T]).filter(a => {
      coreFor(a) match {
        case p +: Nil => matcher.matches(p)
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
  def all[T <: AnyRef : ClassTag](matcher: T => Boolean): Seq[T] =
    plays.allPlayers.filter(ReflectiveHelper.is[T]).map(_.asInstanceOf[T]).filter(a => {
      coreFor(a) match {
        case p +: Nil => matcher(p.asInstanceOf[T])
        case Nil => false
        case l => l.forall(i => matcher(i.asInstanceOf[T]))
      }
    })

  /**
    * Query the role playing graph for all player instances that do conform to the given matcher and return the first found.
    *
    * @param matcher the matcher that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instance, that does conform to the given matcher or an appropriate error
    */
  def one[T <: AnyRef : ClassTag](matcher: RoleQueryStrategy = MatchAny()): Either[TypeError, T] = safeReturn(all[T](matcher), classTag[T].toString).fold(
    l => {
      Left(l)
    }, { case head +: _ =>
      Right(head)
    })

  /**
    * Query the role playing graph for all player instances that do conform to the given function and return the first found.
    *
    * @param matcher the matching function that should match the queried player instance in the role playing graph
    * @tparam T the type of the player instance to query for
    * @return the first player instances, that do conform to the given matcher or an appropriate error
    */
  def one[T <: AnyRef : ClassTag](matcher: T => Boolean): Either[TypeError, T] = safeReturn(all[T](matcher), classTag[T].toString).fold(
    l => {
      Left(l)
    }, { case head +: _ =>
      Right(head)
    })

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
    require(null != role)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

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
    * Explicit helper factory method for creating a new Player instance
    * without the need to relying on the implicit mechanics of Scala.
    *
    * @param obj the player or role that is wrapped into this dynamic player type
    * @return a new Player instance wrapping the given object
    */
  def newPlayer(obj: Object): Player[Object] = {
    require(null != obj)
    new Player(obj)
  }

  /**
    * Removes the given player from the graph.
    * This should remove its binding too!
    *
    * @param player the player to remove
    */
  def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = plays.removePlayer(player)

  /**
    * Returns a Seq of all players
    *
    * @return a Seq of all players
    */
  def allPlayers: Seq[AnyRef] = plays.allPlayers

  @tailrec
  protected final def coreFor(role: AnyRef): Seq[AnyRef] = {
    require(null != role)
    role match {
      case cur: IPlayer[_] => coreFor(cur.wrapped)
      case cur: AnyRef if plays.containsPlayer(cur) =>
        plays.predecessors(cur) match {
          case Nil => Seq(cur)
          case head +: Nil => coreFor(head)
          case r => r
        }
      case _ => Seq(role)
    }
  }

  private[this] def safeReturn[T](seq: Seq[T], typeName: String): Either[TypeError, Seq[T]] = seq match {
    case Nil => Left(TypeNotFound(typeName))
    case s => Right(s)
  }

  protected object PlayerEquality {
    def equalsPlayer[T <: AnyRef : ClassTag](a: IPlayer[T], b: IPlayer[T]): Boolean = (coreFor(a.wrapped), coreFor(b.wrapped)) match {
      case (cl1, cl2) if cl1 equals cl2 => true
      case (_ :+ last, head +: Nil) if head == last => true
      case (head +: Nil, _ :+ last) if head == last => true
      case _ => false
    }

    def equalsAny[T <: AnyRef : ClassTag](a: IPlayer[T], b: Any): Boolean = coreFor(a.wrapped) match {
      case head +: Nil => head == b
      case _ :+ last => last == b
    }
  }

  implicit class Player[T <: AnyRef : ClassTag](override val wrapped: T) extends IPlayer[T](wrapped) with SCROLLDynamic with SCROLLDispatchable {

    override def unary_+ : Player[T] = this

    /**
      * Returns the player of this player instance if this is a role, or this itself.
      *
      * @param dispatchQuery provide this to sort the resulting instances if a role instance is played by multiple core objects
      * @return the player of this player instance if this is a role, or this itself or an appropriate error
      */
    def player(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[TypeError, AnyRef] = dispatchQuery.filter(coreFor(this)) match {
      case elem +: _ => Right(elem)
      case _ => Left(TypeNotFound(this.getClass.toString))
    }

    override def <+>[R <: AnyRef : ClassTag](role: R): Player[T] = play(role)

    override def play[R <: AnyRef : ClassTag](role: R): Player[T] = {
      require(null != role)
      wrapped match {
        case p: Player[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: AnyRef => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case p => throw new RuntimeException(s"Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles! You tried it with '$p'.")
      }
      this
    }

    /**
      * Alias for [[Player.playing]].
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def <=>[R <: AnyRef : ClassTag](role: R): T = playing(role)

    /**
      * Adds a play relation between core and role but always returns the player instance.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def playing[R <: AnyRef : ClassTag](role: R): T = play(role).wrapped

    override def <->[R <: AnyRef : ClassTag](role: R): Player[T] = drop(role)

    override def drop[R <: AnyRef : ClassTag](role: R): Player[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    protected class TransferToBuilder[R <: AnyRef : ClassTag](role: R) {
      def to[P <: AnyRef : ClassTag](player: P): Unit = {
        transferRole[T, P, R](wrapped, player, role)
      }
    }

    /**
      * Transfers a role to another player.
      *
      * @tparam R type of role
      * @param role the role to transfer
      */
    def transfer[R <: AnyRef : ClassTag](role: R): TransferToBuilder[R] =
      new TransferToBuilder[R](role)

    /**
      * Checks if this Player has all of the given facet(s) attached.
      *
      * @param f the facet(s)
      * @return true if this player has all of the given facets attached, false otherwise.
      */
    def hasFacets(f: Enumeration#Value*): Boolean = f.forall(plays.facets(wrapped).contains)

    /**
      * Checks if this Player has at least one of the given facets attached.
      *
      * @param f the facets
      * @return true if this player has at least one of the given facets attached, false otherwise.
      */
    def hasSomeFacet(f: Enumeration#Value*): Boolean = f.exists(plays.facets(wrapped).contains)

    /**
      * Checks of this Player has an extension of the given type.
      * Alias for [[Player.isPlaying]].
      */
    def hasExtension[E <: AnyRef : ClassTag]: Boolean = isPlaying[E]

    /**
      * Checks of this Player is playing a role of the given type R.
      *
      * @tparam R type of role
      * @return true if this player is playing a role of type R, false otherwise. Returns false also, if
      *         the player is not available in the role-playing graph.
      */
    def isPlaying[R <: AnyRef : ClassTag]: Boolean = plays.roles(wrapped).exists(ReflectiveHelper.is[R])

    override def remove(): Unit = plays.removePlayer(this.wrapped)

    override def roles(): Seq[AnyRef] = plays.roles(this.wrapped)

    override def facets(): Seq[Enumeration#Value] = plays.facets(this.wrapped)

    override def predecessors(): Seq[AnyRef] = plays.predecessors(this.wrapped)

    override def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    override def applyDynamic[E](name: String)(args: Any*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] = {
      val core = coreFor(wrapped).last
      dispatchQuery.filter(plays.roles(core)).collectFirst {
        case r if ReflectiveHelper.findMethod(r, name, args).isDefined => (r, ReflectiveHelper.findMethod(r, name, args).get)
      } match {
        case Some((r, fm)) => dispatch(r, fm, args: _*)
        case _ => Left(RoleNotFound(core.toString, name, args))
      }
    }

    override def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, E] = {
      val core = coreFor(wrapped).last
      dispatchQuery.filter(plays.roles(core)).find(ReflectiveHelper.hasMember(_, name)) match {
        case Some(r) => Right(ReflectiveHelper.propertyOf[E](r, name))
        case None => Left(RoleNotFound(core.toString, name, Seq.empty))
      }
    }

    override def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit =
      dispatchQuery.filter(plays.roles(coreFor(wrapped).last)).find(ReflectiveHelper.hasMember(_, name)).foreach(ReflectiveHelper.setPropertyOf(_, name, value))

    override def equals(o: Any): Boolean = o match {
      case other: Player[_] => PlayerEquality.equalsPlayer(this, other)
      case other: Any => PlayerEquality.equalsAny(this, other)
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()

  }

}
