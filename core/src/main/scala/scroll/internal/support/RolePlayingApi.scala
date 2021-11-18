package scroll.internal.support

import scala.reflect.ClassTag

trait RolePlayingApi {

  /** Transfers a role from one core to another.
    *
    * @tparam F
    *   type of core the given role should be removed from
    * @tparam T
    *   type of core the given role should be attached to
    * @tparam R
    *   type of role
    * @param coreFrom
    *   the core the given role should be removed from
    * @param coreTo
    *   the core the given role should be attached to
    * @param role
    *   the role that should be transferred
    */
  def transferRole[F <: AnyRef: ClassTag, T <: AnyRef: ClassTag, R <: AnyRef: ClassTag](
    coreFrom: F,
    coreTo: T,
    role: R
  ): Unit

  /** Adds a play relation between core and role.
    *
    * @tparam C
    *   type of core
    * @tparam R
    *   type of role
    * @param core
    *   the core to add the given role at
    * @param role
    *   the role that should added to the given core
    */
  def addPlaysRelation[C <: AnyRef: ClassTag, R <: AnyRef: ClassTag](core: C, role: R): Unit

  /** Removes the play relation between core and role.
    *
    * @param core
    *   the core the given role should removed from
    * @param role
    *   the role that should removed from the given core
    */
  def removePlaysRelation(core: AnyRef, role: AnyRef): Unit

  /** Removes the given player from the graph. This should remove its binding too!
    *
    * @param player
    *   the player to remove
    */
  def removePlayer(player: AnyRef): Unit

  /** Returns a Seq of all players
    *
    * @return
    *   a Seq of all players
    */
  def allPlayers: Seq[AnyRef]
}
