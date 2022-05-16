package scroll.internal.graph

/** API defining an generic interface for all kind of role graphs.
  */
trait RoleGraph {

  /** RoleGraph given as other would get part of this.
    *
    * @param other
    *   the RoleGraph for integration in this one.
    */
  def addPart(other: RoleGraph): Boolean

  /** Removes all players and plays-relationships specified in other from this RoleGraph.
    *
    * @param other
    *   the RoleGraph all players and plays-relationships specified in should removed from this
    */
  def detach(other: RoleGraph): Unit

  /** Adds a plays relationship between core and role.
    *
    * @param player
    *   the player instance to add the given role
    * @param role
    *   the role instance to add
    */
  def addBinding(player: AnyRef, role: AnyRef): Unit

  /** Removes a plays relationship between core and role.
    *
    * @param player
    *   the player instance to remove the given role from
    * @param role
    *   the role instance to remove
    */
  def removeBinding(player: AnyRef, role: AnyRef): Unit

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

  /** Returns a Seq of all roles attached to the given player (core object).
    *
    * @param player
    *   the player instance to get the roles for
    * @return
    *   a Seq of all roles of core player. Returns an empty Seq if the given player is not in the role-playing graph.
    */
  def roles(player: AnyRef): Seq[AnyRef]

  /** Returns a Seq of all facets attached to the given player (core object).
    *
    * @param player
    *   the player instance to get the facets for
    * @return
    *   a Seq of all facets of core player. Returns an empty Seq if the given player is not in the role-playing graph.
    */
  def facets(player: AnyRef): Seq[Enumeration#Value]

  /** Checks if the role graph contains the given player.
    *
    * @param player
    *   the player instance to check
    * @return
    *   true if the role graph contains the given player, false otherwise
    */
  def containsPlayer(player: AnyRef): Boolean

  /** Returns a list of all predecessors of the given player, i.e. a transitive closure of its cores (deep roles).
    *
    * @param player
    *   the player instance to calculate the cores of
    * @return
    *   a list of all predecessors of the given player
    */
  def predecessors(player: AnyRef): Seq[AnyRef]

  def coreFor(role: AnyRef): Seq[AnyRef]
}
