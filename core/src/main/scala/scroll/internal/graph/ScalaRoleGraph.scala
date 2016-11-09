package scroll.internal.graph

import scroll.internal.support.DispatchQuery

import scala.collection.mutable
import scala.reflect.runtime.universe._

object ScalaRoleGraph {

  sealed trait Node

  case class RolePlayingGraphRoot(players: mutable.ListBuffer[Player]) extends Node

  case class Player(core: Any, role: Any) extends Node {
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

  private val root = RolePlayingGraphRoot(mutable.ListBuffer.empty[Player])

  override def merge(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    other.asInstanceOf[ScalaRoleGraph].root.players.foreach(pl => {
      root.players.contains(pl) match {
        case true => addBinding(pl.core.asInstanceOf[AnyRef], pl.role.asInstanceOf[AnyRef])
        case false => root.players += pl
      }
    })
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
    root.players.foreach(removePlayer)
  }

  private def hasCycle(player: Player): Boolean = getRoles(player.core).contains(player.core)

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.players += Player(player, role)
    if (checkForCycles && hasCycle(Player(player, role))) {
      throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
    }
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.players.find(p => p.core == player && p.role == role).foreach(root.players -= _)
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P): Unit = {
    require(null != player)
    val _ = root.players -= Player(player, null)
  }

  private def getRoles(player: Player, node: Node): Seq[Any] = node match {
    case r: RolePlayingGraphRoot => r.players.flatMap(p => getRoles(player, p))
    case p: Player if p.core == player.core =>
      val result = mutable.ListBuffer(p.role)
      var current = p.role
      while (current != null) {
        root.players.find(_.core == current) match {
          case Some(f) if result.contains(f.role) => current = null
          case Some(f) => current = f.role; result += f.role
          case _ => current = null
        }
      }
      result
    case _ => Seq.empty
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] = {
    require(null != player)
    containsPlayer(player) match {
      case true => getRoles(Player(player, null), root).toSet
      case false => Set(player)
    }
  }

  override def containsPlayer(player: Any): Boolean = root.players.exists(p => p.core == player || p.role == player)

  override def allPlayers: Seq[Any] = root.players.flatMap(p => Seq(p.core, p.role)).distinct

  private def getPredecessors(player: Player, node: Node): Seq[Any] = node match {
    case RolePlayingGraphRoot(players) => players.flatMap(p => getPredecessors(player, p))
    case Player(core, role) if role == player.core => Seq(core) ++ getPredecessors(core)
    case _ => Seq.empty
  }

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[Any] =
    getPredecessors(Player(player, null), root).distinct
}