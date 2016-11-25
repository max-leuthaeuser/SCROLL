package scroll.internal.graph

import scroll.internal.support.DispatchQuery

import scala.collection.mutable
import scala.reflect.ClassTag

object ScalaRoleGraph {

  case class Player(core: Any, role: Any) {
    override def equals(other: scala.Any): Boolean = other match {
      case Player(c, r) if r != null => core == c && role == c
      case Player(c, r) if r == null => core == c
      case _ => false
    }

    override def hashCode(): Int = core.hashCode()
  }

}

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a simple ListBuffer as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class ScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

  import ScalaRoleGraph._

  private var root = mutable.ListBuffer.empty[Player]

  override def merge(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only merge RoleGraphs of the same type!")

    val source = root
    val target = other.asInstanceOf[ScalaRoleGraph].root

    if (source.isEmpty && target.isEmpty) return

    if (source.isEmpty && target.nonEmpty) {
      root = target
      checkCycles()
      return
    }

    if (source.nonEmpty && target.isEmpty) return

    if (source.size < target.size) {
      target.appendAll(source)
      root = target
    } else {
      root.appendAll(target)
    }
    checkCycles()
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
  }

  private def checkCycles(): Unit = {
    if (checkForCycles) {
      root.foreach(pl => if (hasCycle(pl)) {
        throw new RuntimeException(s"Cyclic role-playing relationship for player '$pl' found!")
      })
    }
  }

  private def hasCycle(player: Player): Boolean = getRoles(player.core).contains(player.core)

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root += Player(player, role)
    if (checkForCycles && hasCycle(Player(player, role))) {
      throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
    }
  }

  override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.find(p => p.core == player && p.role == role).foreach(root -= _)
  }

  override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
    require(null != player)
    val _ = root -= Player(player, null)
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] = {
    require(null != player)
    if (containsPlayer(player)) {
      root.flatMap {
        case p: Player if p.core == player =>
          val result = mutable.ListBuffer(p.role)
          var current = p.role
          while (current != null) {
            root.find(_.core == current) match {
              case Some(f) if result.contains(f.role) => current = null
              case Some(f) => current = f.role; result += f.role
              case _ => current = null
            }
          }
          result
        case _ => Seq.empty
      }.toSet
    } else {
      Set(player)
    }
  }

  override def containsPlayer(player: Any): Boolean = root.exists(p => p.core == player || p.role == player)

  override def allPlayers: Seq[Any] = root.flatMap(p => Seq(p.core, p.role)).distinct

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[Any] = root.flatMap {
    case Player(core, role) if role == player => Seq(core) ++ getPredecessors(core)
    case _ => Seq.empty
  }.distinct
}