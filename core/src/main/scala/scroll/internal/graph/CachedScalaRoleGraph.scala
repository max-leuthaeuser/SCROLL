package scroll.internal.graph

import scroll.internal.support.DispatchQuery
import scroll.internal.util.Memoiser

import scala.reflect.ClassTag

class CachedScalaRoleGraph(checkForCycles: Boolean = true) extends ScalaRoleGraph(checkForCycles) with Memoiser {

  private sealed trait KeyOption

  private object Contains extends KeyOption

  private object Predecessors extends KeyOption

  private object Roles extends KeyOption

  private case class Key(obj: Any, opt: KeyOption)

  private class Cache extends Memoised[Key, Set[Any]]

  private val cache = new Cache()

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.addBinding(player, role)
    reset(player)
    reset(role)
  }

  private def reset(o: Any): Unit = {
    Seq(Key(o, Contains), Key(o, Predecessors), Key(o, Roles)).filter(cache.hasBeenComputedAt).foreach(cache.resetAt)
  }

  override def containsPlayer(player: Any): Boolean = {
    val key = Key(player, Contains)
    cache.get(key) match {
      case Some(v) => v.nonEmpty
      case None =>
        if (super.containsPlayer(player)) {
          cache.put(key, Set(player))
          true
        } else {
          cache.put(key, Set.empty)
          false
        }
    }
  }

  override def detach(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only detach RoleGraphs of the same type!")
    super.detach(other)
    cache.reset()
  }

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery): Seq[Any] = {
    val key = Key(player, Predecessors)
    cache.get(key) match {
      case Some(v) => v.toSeq
      case None =>
        val ps = super.getPredecessors(player)
        cache.put(key, ps.toSet)
        ps
    }
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery): Set[Any] = {
    val key = Key(player, Roles)
    cache.get(key) match {
      case Some(v) => v
      case None =>
        val rs = super.getRoles(player)
        cache.put(key, rs)
        rs
    }
  }

  override def merge(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    super.merge(other)
    cache.reset()
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
