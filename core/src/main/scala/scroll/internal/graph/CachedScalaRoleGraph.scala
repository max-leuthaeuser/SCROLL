package scroll.internal.graph

import scroll.internal.graph.CachedScalaRoleGraph._
import scroll.internal.support.DispatchQuery
import scroll.internal.util.Memoiser.IdMemoised
import scala.reflect.runtime.universe._

object CachedScalaRoleGraph {

  sealed trait KeyOption

  object Contains extends KeyOption

  object Predecessors extends KeyOption

  object Roles extends KeyOption

  case class Key(obj: Any, opt: KeyOption)

}

class CachedScalaRoleGraph extends ScalaRoleGraph with IdMemoised[Key, Set[Any]] {
  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    super.addBinding(player, role)
    resetAt(Key(player, Contains))
    resetAt(Key(player, Predecessors))
    resetAt(Key(player, Roles))
    resetAt(Key(role, Contains))
    resetAt(Key(role, Predecessors))
    resetAt(Key(role, Roles))
  }

  override def containsPlayer(player: Any): Boolean = {
    val key = Key(player, Contains)
    get(key) match {
      case Some(v) => v.nonEmpty
      case None =>
        super.containsPlayer(player) match {
          case true =>
            put(key, Set(player))
            true
          case false =>
            put(key, Set.empty)
            false
        }
    }
  }

  override def detach(other: RoleGraph) = {
    super.detach(other)
    reset()
  }

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery): List[Any] = {
    val key = Key(player, Predecessors)
    get(key) match {
      case Some(v) => v.toList
      case None =>
        val ps = super.getPredecessors(player)
        put(key, ps.toSet)
        ps
    }
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery): Set[Any] = {
    val key = Key(player, Roles)
    get(key) match {
      case Some(v) => v
      case None =>
        val rs = super.getRoles(player)
        put(key, rs)
        rs
    }
  }

  override def merge(other: RoleGraph) = {
    super.merge(other)
    reset()
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) = {
    super.removeBinding(player, role)
    resetAt(Key(player, Contains))
    resetAt(Key(player, Predecessors))
    resetAt(Key(player, Roles))
    resetAt(Key(role, Contains))
    resetAt(Key(role, Predecessors))
    resetAt(Key(role, Roles))
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P) = {
    super.removePlayer(player)
    resetAt(Key(player, Contains))
    resetAt(Key(player, Predecessors))
    resetAt(Key(player, Roles))
  }
}
