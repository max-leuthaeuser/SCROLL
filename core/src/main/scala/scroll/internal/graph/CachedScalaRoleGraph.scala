package scroll.internal.graph

import scroll.internal.support.DispatchQuery
import scroll.internal.util.Memoiser

import scala.reflect.ClassTag

class CachedScalaRoleGraph(checkForCycles: Boolean = true) extends ScalaRoleGraph(checkForCycles) with Memoiser {

  private class BooleanCache extends Memoised[Any, Boolean]

  private class SeqCache extends Memoised[Any, Seq[Any]]

  private val containsCache = new BooleanCache()
  private val predCache = new SeqCache()
  private val rolesCache = new SeqCache()

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    super.addBinding(player, role)
    reset(player)
    reset(role)
  }

  private def resetAll(): Unit = {
    containsCache.reset()
    predCache.reset()
    rolesCache.reset()
  }

  private def reset(o: Any): Unit = {
    containsCache.resetAt(o)
    predCache.resetAt(o)
    rolesCache.resetAt(o)
  }

  override def containsPlayer(player: Any): Boolean =
    containsCache.getAndPutWithDefault(player, super.containsPlayer(player))

  override def detach(other: RoleGraph): Unit = {
    require(other.isInstanceOf[CachedScalaRoleGraph], "You can only detach RoleGraphs of the same type!")
    super.detach(other)
    resetAll()
  }

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery): Seq[Any] =
    predCache.getAndPutWithDefault(player, super.getPredecessors(player))

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery): Seq[Any] =
    rolesCache.getAndPutWithDefault(player, super.getRoles(player))

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
