package scroll.internal.compartment.impl

import scroll.internal.compartment.CompartmentApi
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.impl.SCROLLDispatchable
import scroll.internal.errors.SCROLLErrors.TypeError
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.graph.impl.ScalaRoleGraphProxy
import scroll.internal.support._
import scroll.internal.support.impl.CompartmentRelations
import scroll.internal.support.impl.PlayerEquality
import scroll.internal.support.impl.Relationships
import scroll.internal.support.impl.RoleConstraints
import scroll.internal.support.impl.RoleGroups
import scroll.internal.support.impl.RolePlaying
import scroll.internal.support.impl.RoleQueries
import scroll.internal.support.impl.RoleRestrictions
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag

/** Partly implements the API for Compartments. See the subclasses [[Compartment]] and [[MultiCompartment]] for
  * examples of a full implementation.
  */
abstract class AbstractCompartment() extends CompartmentApi {

  override lazy val roleGraph: RoleGraphProxyApi          = new ScalaRoleGraphProxy()
  override lazy val roleConstraints: RoleConstraintsApi   = new RoleConstraints(roleGraph)
  override lazy val roleRestrictions: RoleRestrictionsApi = new RoleRestrictions()
  override lazy val rolePlaying: RolePlayingApi           = new RolePlaying(roleGraph, roleRestrictions)
  override lazy val roleQueries: RoleQueriesApi           = new RoleQueries(roleGraph)

  override lazy val compartmentRelations: CompartmentRelationsApi = new CompartmentRelations(
    roleGraph
  )
  override lazy val roleRelationships: RelationshipsApi = new Relationships(roleQueries)
  override lazy val roleGroups: RoleGroupsApi           = new RoleGroups(roleGraph)
  override lazy val playerEquality: PlayerEqualityApi   = new PlayerEquality(roleGraph)

  implicit def either2TorException[T](either: Either[_, T]): T =
    either.fold(l => throw new RuntimeException(l.toString), r => r)

  protected def applyDispatchQuery(dispatchQuery: DispatchQuery, on: AnyRef): Seq[AnyRef] =
    roleGraph.plays.coreFor(on).lastOption match {
      case Some(core) => dispatchQuery.filter(core +: roleGraph.plays.roles(core))
      case _          => Seq.empty[AnyRef]
    }

  /** Explicit helper factory method for creating a new Player instance
    * without the need to relying on the implicit mechanics of Scala.
    *
    * @param obj the player or role that is wrapped into this dynamic player type
    * @return a new Player instance wrapping the given object
    */
  def newPlayer[W <: AnyRef: ClassTag](obj: W): IPlayer[W, _]

  /** Wrapper class to add basic functionality to roles and its players as unified types.
    *
    * @param wrapped the player or role that is wrapped into this dynamic type
    * @tparam W type of wrapped object
    */
  abstract class IPlayer[+W <: AnyRef: ClassTag, +T <: IPlayer[W, T]](val wrapped: W)
      extends SCROLLDispatchable
        with Dynamic {

    self: T =>

    /** Applies lifting to IPlayer
      *
      * @return an lifted IPlayer instance with the calling object as wrapped.
      */
    def unary_+ : T = this

    /** Returns the player of this player instance if this is a role, or this itself.
      *
      * @param dispatchQuery provide this to sort the resulting instances if a role instance is played by multiple core objects
      * @return the player of this player instance if this is a role, or this itself or an appropriate error
      */
    def player(using dispatchQuery: DispatchQuery = DispatchQuery()): Either[TypeError, AnyRef] =
      dispatchQuery.filter(roleGraph.plays.coreFor(this)) match {
        case elem +: _ => Right(elem)
        case _         => Left(TypeNotFound(this.getClass))
      }

    /** Adds a play relation between core and role.
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def play[R <: AnyRef: ClassTag](role: R): T = {
      require(null != role)
      wrapped match {
        case p: IPlayer[_, _] => rolePlaying.addPlaysRelation[W, R](p.wrapped.asInstanceOf[W], role)
        case p: AnyRef        => rolePlaying.addPlaysRelation[W, R](p.asInstanceOf[W], role)
        case p =>
          throw new RuntimeException(
            s"Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles! You tried it with '$p'."
          )
      }
      this
    }

    /** Alias for [[IPlayer.playing]].
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def <=>[R <: AnyRef: ClassTag](role: R): W = playing(role)

    /** Adds a play relation between core and role but always returns the player instance.
      *
      * @tparam R type of role
      * @param role the role that should played
      * @return the player instance
      */
    def playing[R <: AnyRef: ClassTag](role: R): W = play(role).wrapped

    /** Alias for [[IPlayer.drop]].
      *
      * @param role the role that should be removed
      * @return this
      */
    def <->(role: AnyRef): T = drop(role)

    /** Removes the play relation between core and role.
      *
      * @param role the role that should be removed
      * @return this
      */
    def drop(role: AnyRef): T = {
      rolePlaying.removePlaysRelation(wrapped, role)
      this
    }

    protected class TransferToBuilder[R <: AnyRef: ClassTag](role: R) {

      def to[P <: AnyRef: ClassTag](player: P): Unit =
        rolePlaying.transferRole[W, P, R](wrapped, player, role)
    }

    /** Transfers a role to another player.
      *
      * @tparam R type of role
      * @param role the role to transfer
      */
    def transfer[R <: AnyRef: ClassTag](role: R): TransferToBuilder[R] =
      new TransferToBuilder[R](role)

    /** Checks if this IPlayer has all of the given facet(s) attached.
      *
      * @param f the facet(s)
      * @return true if this player has all of the given facets attached, false otherwise.
      */
    def hasFacets(f: Enumeration#Value*): Boolean =
      f.forall(roleGraph.plays.facets(wrapped).contains)

    /** Checks if this IPlayer has at least one of the given facets attached.
      *
      * @param f the facets
      * @return true if this player has at least one of the given facets attached, false otherwise.
      */
    def hasSomeFacet(f: Enumeration#Value*): Boolean =
      f.exists(roleGraph.plays.facets(wrapped).contains)

    /** Checks of this IPlayer has an extension of the given type.
      * Alias for [[IPlayer.isPlaying]].
      */
    def hasExtension[E <: AnyRef: ClassTag]: Boolean = isPlaying[E]

    /** Checks of this IPlayer is playing a role of the given type R.
      *
      * @tparam R type of role
      * @return true if this player is playing a role of type R, false otherwise. Returns false also, if
      *         the player is not available in the role-playing graph.
      */
    def isPlaying[R <: AnyRef: ClassTag]: Boolean =
      roleGraph.plays.roles(wrapped).exists(ReflectiveHelper.is[R])

    /** Alias for [[IPlayer.play]].
      *
      * @tparam R type of role
      * @param role the role that should be played
      * @return this
      */
    def <+>[R <: AnyRef: ClassTag](role: R): T = play(role)

    /** Removes this player from the graph.
      */
    def remove(): Unit = roleGraph.plays.removePlayer(this.wrapped)

    /** Returns a Seq of all roles attached to this player.
      *
      * @return a Seq of all roles of this player. Returns an empty Seq if this player is not in the role-playing graph.
      */
    def roles(): Seq[AnyRef] = roleGraph.plays.roles(this.wrapped)

    /** Returns a Seq of all facets attached to this player.
      *
      * @return a Seq of all facets of this player including the player object itself. Returns an empty Seq if this player is not in the role-playing graph.
      */
    def facets(): Seq[Enumeration#Value] = roleGraph.plays.facets(this.wrapped)

    /** Returns a list of all predecessors of this player, i.e., a transitive closure
      * of its cores (deep roles).
      *
      * @return a list of all predecessors of this player
      */
    def predecessors(): Seq[AnyRef] = roleGraph.plays.predecessors(this.wrapped)

    override def equals(o: Any): Boolean =
      o match {
        case other: IPlayer[_, _] => playerEquality.equalsPlayer(this, other)
        case other: Any           => playerEquality.equalsAny(this, other)
        case _                    => false // default case
      }

    override def hashCode(): Int = wrapped.hashCode()

  }

}
