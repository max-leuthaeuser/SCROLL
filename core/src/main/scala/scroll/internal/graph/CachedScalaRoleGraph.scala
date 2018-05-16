package scroll.internal.graph

import scroll.internal.util.Memoiser

import scala.reflect.ClassTag

class CachedScalaRoleGraph(checkForCycles: Boolean = true) extends ScalaRoleGraph(checkForCycles) with Memoiser {

  private class BooleanCache extends Memoised[AnyRef, Boolean]

  private class SeqCache[E] extends Memoised[AnyRef, Seq[E]]

  private val containsCache = new BooleanCache()
  private val predCache = new SeqCache[AnyRef]()
  private val rolesCache = new SeqCache[AnyRef]()
  private val facetsCache = new SeqCache[Enumeration#Value]()

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.addBinding(player, role)
    reset(player)
    reset(role)
  }

  private def resetAll(): Unit = {
    containsCache.reset()
    predCache.reset()
    rolesCache.reset()
    facetsCache.reset()
  }

  private def reset(o: AnyRef): Unit = {
    containsCache.resetAt(o)
    predCache.resetAt(o)
    rolesCache.resetAt(o)
    facetsCache.resetAt(o)
  }

  override def containsPlayer(player: AnyRef): Boolean =
    containsCache.getAndPutWithDefault(player, super.containsPlayer(player))

  override def detach(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only detach RoleGraphs of the same type!")
    super.detach(other)
    resetAll()
  }

  override def predecessors(player: AnyRef): Seq[AnyRef] =
    predCache.getAndPutWithDefault(player, super.predecessors(player))

  override def roles(player: AnyRef): Seq[AnyRef] =
    rolesCache.getAndPutWithDefault(player, super.roles(player))

  override def facets(player: AnyRef): Seq[Enumeration#Value] =
    facetsCache.getAndPutWithDefault(player, super.facets(player))

  override def combine(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    super.combine(other)
    resetAll()
  }

  override def addPart(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    super.addPart(other)
    resetAll()
  }

  override def addPartAndCombine(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    super.addPartAndCombine(other)
    resetAll()
  }

  override def merge(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    super.merge(other)
    resetAll()
  }

  override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.removeBinding(player, role)
    reset(player)
    reset(role)
  }

  override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
    super.removePlayer(player)
    reset(player)
  }
}
