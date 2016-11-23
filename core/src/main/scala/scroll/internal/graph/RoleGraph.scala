package scroll.internal.graph

import scroll.internal.support.DispatchQuery

import scala.reflect.ClassTag

/**
  * Trait defining an generic interface for all kind of role graphs.
  */
trait RoleGraph {
  /**
    * Merges this with another RoleGraph given as other.
    *
    * @param other the RoleGraph to merge with.
    */
  def merge(other: RoleGraph): Unit

  /**
    * Removes all players and plays-relationships specified in other from this RoleGraph.
    *
    * @param other the RoleGraph all players and plays-relationships specified in should removed from this
    */
  def detach(other: RoleGraph): Unit

  /**
    * Adds a plays relationship between core and role.
    *
    * @tparam P type of the player
    * @tparam R type of the role
    * @param player the player instance to add the given role
    * @param role   the role instance to add
    */
  def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit

  /**
    * Removes a plays relationship between core and role.
    *
    * @param player the player instance to remove the given role from
    * @param role   the role instance to remove
    */
  def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit

  /**
    * Removes the given player from the graph.
    * This should remove its binding too!
    *
    * @param player the player to remove
    */
  def removePlayer[P <: AnyRef : ClassTag](player: P): Unit

  /**
    * Returns a Seq of all players
    *
    * @return a Seq of all players
    */
  def allPlayers: Seq[Any]

  /**
    * Returns a Set of all roles attached to the given player (core object).
    *
    * @param player        the player instance to get the roles for
    * @param dispatchQuery the strategy used to get all roles while traversing the role-playing graph, standard is DFS
    * @return a Set of all roles of core
    */
  def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any]

  /**
    * Checks if the role graph contains the given player.
    *
    * @param player the player instance to check
    * @return true if the role graph contains the given player, false otherwise
    */
  def containsPlayer(player: Any): Boolean

  /**
    * Returns a list of all predecessors of the given player, i.e. a transitive closure
    * of its cores (deep roles).
    *
    * @param player        the player instance to calculate the cores of
    * @param dispatchQuery the strategy used to get all predecessors while traversing the role-playing graph, standard is DFS
    * @return a list of all predecessors of the given player
    */
  def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[Any]
}