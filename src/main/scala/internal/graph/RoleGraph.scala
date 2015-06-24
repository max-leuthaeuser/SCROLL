package internal.graph

/**
 * Trait defining an generic interface for all kind of role graphs.
 *
 * @tparam N Type of roles as Nodes
 */
trait RoleGraph[N] {

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
   * Returns a Set of all roles attached to the given player (core object).
   *
   * @param player the player instance to get the roles for
   * @return a Set of all roles of core
   */
  def getRoles(player: N): Seq[N]

  /**
   * Checks if the role graph contains the given player.
   *
   * @param player the player instance to check
   * @return true if the role graph contains the given player, false otherwise
   */
  def containsPlayer(player: Any): Boolean
}