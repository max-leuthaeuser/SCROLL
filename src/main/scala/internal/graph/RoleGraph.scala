package internal.graph

// N: Type of roles as Nodes
trait RoleGraph[N] {
  def addBinding(core: N, role: N)
  def removeBinding(core: N, role: N)
  def removePlayer(player: N)
  def getRoles(core: N): Set[N]
}