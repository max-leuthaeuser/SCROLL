package internal.graph

/**
 * Trait defining an generic interface for all kind of role graphs.
 *
 * @param <N> Type of roles as Nodes
 */
trait RoleGraph[N] {

  /**
   * Adds a plays relationship between core and role.
   *
   * @param core
   * @param role
   */
  def addBinding(core: N, role: N)

  /**
   * Removes a plays relationship between core and role.
   *
   * @param core
   * @param role
   */
  def removeBinding(core: N, role: N)

  /**
   * Removes the given player from the graph.
   * This should remove its binding too!
   *
   * @param player
   */
  def removePlayer(player: N)

  /**
   * Returns a Set of all roles attached to the given player (core object).
   *
   * @param core
   * @return a Set of all roles of core
   */
  def getRoles(core: N): Set[N]
}