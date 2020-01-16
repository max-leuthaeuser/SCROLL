package scroll.internal.graph.impl

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import scroll.internal.graph.RoleGraph

object CachedScalaRoleGraph {
  def copyFrom(from: ScalaRoleGraph, checkForCycles: Boolean): CachedScalaRoleGraph = new CachedScalaRoleGraph(from.root, checkForCycles)
}

class CachedScalaRoleGraph(root: MutableGraph[Object] = GraphBuilder.directed().build[Object](),
                           checkForCycles: Boolean = true) extends ScalaRoleGraph(root, checkForCycles) {

  import scroll.internal.util.Memoiser._

  private[this] val containsCache = buildCache[AnyRef, java.lang.Boolean](super.containsPlayer)
  private[this] val predCache = buildCache[AnyRef, Seq[AnyRef]](super.predecessors)
  private[this] val rolesCache = buildCache[AnyRef, Seq[AnyRef]](super.roles)
  private[this] val facetsCache = buildCache[AnyRef, Seq[Enumeration#Value]](super.facets)

  override def addBinding(player: AnyRef, role: AnyRef): Unit = {
    super.addBinding(player, role)
    resetAfterBinding(player, role)
  }

  private[this] def resetAfterBinding(player: AnyRef, role: AnyRef): Unit = {
    predecessors(player).foreach(reset)
    roles(role).foreach(reset)
    reset(role)
    reset(player)
  }

  private[this] def resetAll(): Unit = {
    containsCache.invalidateAll()
    predCache.invalidateAll()
    rolesCache.invalidateAll()
    facetsCache.invalidateAll()
  }

  private[this] def reset(o: AnyRef): Unit = {
    containsCache.invalidate(o)
    predCache.invalidate(o)
    rolesCache.invalidate(o)
    facetsCache.invalidate(o)
  }

  override def containsPlayer(player: AnyRef): Boolean = containsCache.get(player)

  override def detach(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], MERGE_MESSAGE)
    super.detach(other)
    resetAll()
  }

  override def predecessors(player: AnyRef): Seq[AnyRef] = predCache.get(player)

  override def roles(player: AnyRef): Seq[AnyRef] = rolesCache.get(player)

  override def facets(player: AnyRef): Seq[Enumeration#Value] = facetsCache.get(player)

  override def addPart(other: RoleGraph): Boolean = {
    require(other.isInstanceOf[CachedScalaRoleGraph], MERGE_MESSAGE)
    if (super.addPart(other)) {
      resetAll()
      true
    } else {
      false
    }
  }

  override def removeBinding(player: AnyRef, role: AnyRef): Unit = {
    super.removeBinding(player, role)
    resetAfterBinding(player, role)
  }

  override def removePlayer(player: AnyRef): Unit = {
    roles(player).foreach(reset)
    predecessors(player).foreach(reset)
    super.removePlayer(player)
    reset(player)
  }
}
