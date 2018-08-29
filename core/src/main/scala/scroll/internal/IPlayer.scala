package scroll.internal

import scala.reflect.ClassTag

/**
  * Wrapper class to add basic functionality to roles and its players as unified types.
  *
  * @param wrapped the player or role that is wrapped into this dynamic type
  * @tparam T type of wrapped object
  */
abstract class IPlayer[T <: AnyRef : ClassTag](val wrapped: T) {
  /**
    * Applies lifting to Player
    *
    * @return an lifted Player instance with the calling object as wrapped.
    */
  def unary_+ : IPlayer[T] = this

  /**
    * Adds a play relation between core and role.
    *
    * @tparam R type of role
    * @param role the role that should be played
    * @return this
    */
  def play[R <: AnyRef : ClassTag](role: R): IPlayer[T]

  /**
    * Alias for [[IPlayer.play]].
    *
    * @tparam R type of role
    * @param role the role that should be played
    * @return this
    */
  def <+>[R <: AnyRef : ClassTag](role: R): IPlayer[T]

  /**
    * Removes the play relation between core and role.
    *
    * @param role the role that should be removed
    * @return this
    */
  def drop[R <: AnyRef : ClassTag](role: R): IPlayer[T]

  /**
    * Alias for [[IPlayer.drop]].
    *
    * @param role the role that should be removed
    * @return this
    */
  def <->[R <: AnyRef : ClassTag](role: R): IPlayer[T]

  /**
    * Removes this player from the graph.
    */
  def remove(): Unit

  /**
    * Returns a Seq of all roles attached to this player.
    *
    * @return a Seq of all roles of this player including the player object itself. Returns an empty Seq if this player is not in the role-playing graph.
    */
  def roles(): Seq[AnyRef]

  /**
    * Returns a Seq of all facets attached to this player.
    *
    * @return a Seq of all facets of this player including the player object itself. Returns an empty Seq if this player is not in the role-playing graph.
    */
  def facets(): Seq[Enumeration#Value]

  /**
    * Returns a list of all predecessors of this player, i.e. a transitive closure
    * of its cores (deep roles).
    *
    * @return a list of all predecessors of this player
    */
  def predecessors(): Seq[AnyRef]

  override def equals(o: Any): Boolean

  override def hashCode(): Int
}
