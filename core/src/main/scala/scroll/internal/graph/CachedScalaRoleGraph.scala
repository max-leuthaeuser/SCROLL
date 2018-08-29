package scroll.internal.graph

import scroll.internal.util.Memoiser

import scala.reflect.ClassTag

class CachedScalaRoleGraph(checkForCycles: Boolean = true) extends ScalaRoleGraph(checkForCycles) with Memoiser {

  private[this] class BooleanCache extends Memoised[AnyRef, Boolean]

  private[this] class SeqCache[E] extends Memoised[AnyRef, Seq[E]]

  private[this] val containsCache = new BooleanCache()
  private[this] val predCache = new SeqCache[AnyRef]()
  private[this] val rolesCache = new SeqCache[AnyRef]()
  private[this] val facetsCache = new SeqCache[Enumeration#Value]()

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.addBinding(player, role)
    predecessors(player).foreach(reset)
    roles(role).foreach(reset)
    reset(player)
  }

  private[this] def resetAll(): Unit = {
    containsCache.reset()
    predCache.reset()
    rolesCache.reset()
    facetsCache.reset()
  }

  private[this] def reset(o: AnyRef): Unit = {
    containsCache.resetAt(o)
    predCache.resetAt(o)
    rolesCache.resetAt(o)
    facetsCache.resetAt(o)
  }

  override def containsPlayer(player: AnyRef): Boolean =
    containsCache.getAndPutWithDefault(player, super.containsPlayer(player))

  override def detach(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], MERGE_MESSAGE)
    super.detach(other)
    resetAll()
  }

  override def predecessors(player: AnyRef): Seq[AnyRef] =
    predCache.getAndPutWithDefault(player, super.predecessors(player))

  override def roles(player: AnyRef): Seq[AnyRef] =
    rolesCache.getAndPutWithDefault(player, super.roles(player))

  override def facets(player: AnyRef): Seq[Enumeration#Value] =
    facetsCache.getAndPutWithDefault(player, super.facets(player))

  override def addPart(other: RoleGraph): Boolean = {
    require(other.isInstanceOf[CachedScalaRoleGraph], MERGE_MESSAGE)
    if (super.addPart(other)) {
      resetAll()
      true
    } else {
      false
    }
  }

  override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.removeBinding(player, role)
    predecessors(player).foreach(reset)
    roles(role).foreach(reset)
    reset(player)
  }

  override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
    roles(player).foreach(reset)
    predecessors(player).foreach(reset)
    super.removePlayer(player)
    reset(player)
  }
}
