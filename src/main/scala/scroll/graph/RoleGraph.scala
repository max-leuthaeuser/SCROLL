package scroll.graph

import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultEdge

/**
 * Trait defining an generic interface for all kind of role graphs.
 *
 * @tparam N Type of roles as Nodes
 */
trait RoleGraph[N] {
  val store: DirectedGraph[N, DefaultEdge]

  /**
   * Merges this with another RoleGraph given as other.
   *
   * @param other the RoleGraph to merge with.
   */
  def merge(other: RoleGraph[N])

  /**
   * Removes all players and plays-relationships specified in other from this RoleGraph.
   *
   * @param other the RoleGraph all players and plays-relationships specified in should removed from this
   */
  def detach(other: RoleGraph[N])

  /**
   * Adds a plays relationship between core and role.
   *
   * @param player the player instance to add the given role
   * @param role the role instance to add
   */
  def addBinding(player: N, role: N)

  /**
   * Removes a plays relationship between core and role.
   *
   * @param player the player instance to remove the given role from
   * @param role the role instance to remove
   */
  def removeBinding(player: N, role: N)

  /**
   * Removes the given player from the graph.
   * This should remove its binding too!
   *
   * @param player the player to remove
   */
  def removePlayer(player: N)

  /**
   * Returns a Seq of all players
   *
   * @return a Seq of all players
   */
  def allPlayers: Seq[N]

  /**
   * Returns a Set of all roles attached to the given player (core object).
   *
   * @param player the player instance to get the roles for
   * @return a Set of all roles of core
   */
  def getRoles(player: N): Set[N]

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
   * @param player the player instance to calculate the cores of
   * @return a list of all predecessors of the given player
   */
  def getPredecessors(player: N): List[N]
}