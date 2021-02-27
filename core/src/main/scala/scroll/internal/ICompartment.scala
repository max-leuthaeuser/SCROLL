package scroll.internal

import scroll.internal.errors.SCROLLErrors.TypeError
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.internal.graph.HasRoleGraph
import scroll.internal.support._
import scroll.internal.support.UnionTypes.RoleUnionTypes
import scroll.internal.util.ReflectiveHelper

import scala.annotation.tailrec
import scala.reflect.ClassTag

/**
  * Interface for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  */
trait ICompartment extends RoleConstraints
  with RoleRestrictions
  with RoleGroups
  with Relationships
  with CompartmentRelations
  with QueryStrategies
  with RoleQueries
  with PlayerEquality
  with RoleUnionTypes
  with HasRoleGraph {

  implicit def either2TorException[T](either: Either[_, T]): T = either.fold(
    l => {
      throw new RuntimeException(l.toString)
    }, r => {
      r
    })

  protected def safeReturn[T](seq: Seq[T], typeName: String): Either[TypeError, Seq[T]] = seq match {
    case Nil => Left(TypeNotFound(typeName))
    case s => Right(s)
  }

  protected def safeReturnHead[T](seq: Seq[T], typeName: String): Either[TypeError, T] = safeReturn(seq, typeName).fold(
    l => {
      Left(l)
    }, { case head +: _ =>
      Right(head)
    })

  @tailrec
  protected final def coreFor(role: AnyRef): Seq[AnyRef] = {
    require(null != role)
    role match {
      case cur: IPlayer[_, _] => coreFor(cur.wrapped)
      case cur: AnyRef if plays.containsPlayer(cur) =>
        plays.predecessors(cur) match {
          case Nil => Seq(cur)
          case head +: Nil => coreFor(head)
          case r => r
        }
      case _ => Seq.empty[AnyRef]
    }
  }

  protected def applyDispatchQuery(dispatchQuery: DispatchQuery, on: AnyRef): Seq[AnyRef] =
    coreFor(on).lastOption match {
      case Some(core) => dispatchQuery.filter(core +: plays.roles(core))
      case _ => Seq.empty[AnyRef]
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
  protected def transferRole[F <: AnyRef, T <: AnyRef, R <: AnyRef : ClassTag](coreFrom: F, coreTo: T, role: R): Unit = {
    require(null != coreFrom)
    require(null != coreTo)
    require(null != role)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    removePlaysRelation(coreFrom, role)
    addPlaysRelation[T, R](coreTo, role)
  }

  /**
    * Adds a play relation between core and role.
    *
    * @tparam C type of core
    * @tparam R type of role
    * @param core the core to add the given role at
    * @param role the role that should added to the given core
    */
  protected def addPlaysRelation[C <: AnyRef, R <: AnyRef : ClassTag](core: C, role: R): Unit = {
    require(null != core)
    require(null != role)
    validate[R](core, role)
    plays.addBinding(core, role)
  }

  /**
    * Removes the play relation between core and role.
    *
    * @param core the core the given role should removed from
    * @param role the role that should removed from the given core
    */
  protected def removePlaysRelation(core: AnyRef, role: AnyRef): Unit = {
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
  def newPlayer[W <: AnyRef : ClassTag](obj: W): IPlayer[W, _]

  /**
    * Removes the given player from the graph.
    * This should remove its binding too!
    *
    * @param player the player to remove
    */
  def removePlayer(player: AnyRef): Unit = plays.removePlayer(player)

  /**
    * Returns a Seq of all players
    *
    * @return a Seq of all players
    */
  def allPlayers: Seq[AnyRef] = plays.allPlayers

  /**
    * Wrapper class to add basic functionality to roles and its players as unified types.
    *
    * @param wrapped the player or role that is wrapped into this dynamic type
    * @tparam W type of wrapped object
    */
  abstract class IPlayer[+W <: AnyRef, +T <: IPlayer[W, T]](val wrapped: W) extends SCROLLDispatchable with Dynamic {

    self: T =>

    /**
      * Applies lifting to IPlayer
      *
      * @return an lifted IPlayer instance with the calling object as wrapped.
      */
    def unary_+ : T = this

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

    /**
      * Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def play[R <: AnyRef : ClassTag](role: R): T = {
      require(null != role)
      wrapped match {
        case p: IPlayer[_, _] => addPlaysRelation[W, R](p.wrapped.asInstanceOf[W], role)
        case p: AnyRef => addPlaysRelation[W, R](p.asInstanceOf[W], role)
        case p => throw new RuntimeException(s"Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles! You tried it with '$p'.")
      }
      this
    }

    /**
      * Alias for [[IPlayer.playing]].
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def <=>[R <: AnyRef : ClassTag](role: R): W = playing(role)

    /**
      * Adds a play relation between core and role but always returns the player instance.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def playing[R <: AnyRef : ClassTag](role: R): W = play(role).wrapped

    /**
      * Alias for [[IPlayer.drop]].
      *
      * @param role the role that should be removed
      * @return this
      */
    def <->(role: AnyRef): T = drop(role)

    /**
      * Removes the play relation between core and role.
      *
      * @param role the role that should be removed
      * @return this
      */
    def drop(role: AnyRef): T = {
      removePlaysRelation(wrapped, role)
      this
    }

    protected class TransferToBuilder[R <: AnyRef : ClassTag](role: R) {
      def to[P <: AnyRef](player: P): Unit = {
        transferRole[W, P, R](wrapped, player, role)
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
      * Checks if this IPlayer has all of the given facet(s) attached.
      *
      * @param f the facet(s)
      * @return true if this player has all of the given facets attached, false otherwise.
      */
    def hasFacets(f: Enumeration#Value*): Boolean = f.forall(plays.facets(wrapped).contains)

    /**
      * Checks if this IPlayer has at least one of the given facets attached.
      *
      * @param f the facets
      * @return true if this player has at least one of the given facets attached, false otherwise.
      */
    def hasSomeFacet(f: Enumeration#Value*): Boolean = f.exists(plays.facets(wrapped).contains)

    /**
      * Checks of this IPlayer has an extension of the given type.
      * Alias for [[IPlayer.isPlaying]].
      */
    def hasExtension[E <: AnyRef : ClassTag]: Boolean = isPlaying[E]

    /**
      * Checks of this IPlayer is playing a role of the given type R.
      *
      * @tparam R type of role
      * @return true if this player is playing a role of type R, false otherwise. Returns false also, if
      *         the player is not available in the role-playing graph.
      */
    def isPlaying[R <: AnyRef : ClassTag]: Boolean = plays.roles(wrapped).exists(ReflectiveHelper.is[R])

    /**
      * Alias for [[IPlayer.play]].
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def <+>[R <: AnyRef : ClassTag](role: R): T = play(role)

    /**
      * Removes this player from the graph.
      */
    def remove(): Unit = plays.removePlayer(this.wrapped)

    /**
      * Returns a Seq of all roles attached to this player.
      *
      * @return a Seq of all roles of this player. Returns an empty Seq if this player is not in the role-playing graph.
      */
    def roles(): Seq[AnyRef] = plays.roles(this.wrapped)

    /**
      * Returns a Seq of all facets attached to this player.
      *
      * @return a Seq of all facets of this player including the player object itself. Returns an empty Seq if this player is not in the role-playing graph.
      */
    def facets(): Seq[Enumeration#Value] = plays.facets(this.wrapped)

    /**
      * Returns a list of all predecessors of this player, i.e., a transitive closure
      * of its cores (deep roles).
      *
      * @return a list of all predecessors of this player
      */
    def predecessors(): Seq[AnyRef] = plays.predecessors(this.wrapped)

    override def equals(o: Any): Boolean = o match {
      case other: IPlayer[_, _] => equalsPlayer(this, other)
      case other: Any => equalsAny(this, other)
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}
