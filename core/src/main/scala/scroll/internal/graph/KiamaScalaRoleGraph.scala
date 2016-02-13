package scroll.internal.graph

import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.kiama.attribution.ParamAttributeKey
import org.kiama.util.TreeNode
import scroll.internal.support.DispatchQuery

import scala.collection.mutable
import scala.reflect.runtime.universe._
import org.kiama.attribution.Attribution._

object KiamaScalaRoleGraph {

  sealed trait Node extends TreeNode

  case class RolePlayingGraphRoot(var players: mutable.ListBuffer[Player]) extends Node

  case class Player(core: Any, role: Any) extends Node {
    override def equals(other: scala.Any): Boolean = other match {
      case Player(c, r) if r != null => core == c && role == c
      case Player(c, r) if r == null => core == c
      case _ => false
    }

    override def hashCode(): Int = core.hashCode()
  }

}

class KiamaScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

  import KiamaScalaRoleGraph._

  private lazy val root = RolePlayingGraphRoot(mutable.ListBuffer.empty)

  private def resetAll() {
    Set(kiama_hasCycle, kiama_containsPlayer, kiama_allPlayers, kiama_getRoles, kiama_getPredecessors).foreach(_.reset())
  }

  private lazy val kiama_hasCycleDef: Node => Boolean = {
    case RolePlayingGraphRoot(players) =>
      players.exists(kiama_hasCycle)
    case p@Player(core, role) if role != null =>
      kiama_getRoles(p)(root).contains(p) || kiama_hasCycle(Player(role, null))
    case _ => false
  }

  private lazy val kiama_hasCycle = attr(kiama_hasCycleDef)

  private def kiama_addBinding(player: Player) {
    root.players += player
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    kiama_addBinding(Player(player, role))
    if (checkForCycles) kiama_hasCycle.reset()
    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()
    kiama_getRoles.reset()
    kiama_getPredecessors.reset()
    if (checkForCycles) if (kiama_hasCycle(Player(player, role))) throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
  }

  private lazy val kiama_getRolesDef: Player => Node => Seq[Any] = {
    case player: Player => {
      case node: Node => node match {
        case r: RolePlayingGraphRoot => r.players.flatMap(p => kiama_getRolesDef(player)(p))
        case p: Player if p.core == player.core => Seq(p.role) ++ getRoles(p.role)
        case _ => Seq.empty
      }
    }
  }

  private lazy val kiama_getRoles = paramAttr(kiama_getRolesDef)

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] =
    containsPlayer(player) match {
      case true => kiama_getRoles(Player(player, null))(root).toSet
      case false => Set(player)
    }

  private lazy val kiama_allPlayersDef: RolePlayingGraphRoot => Seq[Any] = _.players.flatMap(p => Seq(p.core, p.role)).toSeq

  private lazy val kiama_allPlayers = attr(kiama_allPlayersDef)

  override def allPlayers: Seq[Any] = kiama_allPlayers(root).distinct

  private lazy val kiama_containsPlayerDef: Player => RolePlayingGraphRoot => Boolean = {
    case player: Player => _.players.exists(p => p.core == player.core || p.role == player.core)
  }

  private lazy val kiama_containsPlayer = paramAttr(kiama_containsPlayerDef)

  override def containsPlayer(player: Any): Boolean = kiama_containsPlayer(Player(player, null))(root)

  private def kiama_detach(other: RoleGraph) {
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
    root.players.foreach(kiama_removePlayer)
  }

  override def detach(other: RoleGraph) {
    kiama_detach(other)
    resetAll()
  }

  private lazy val kiama_getPredecessorsDef: Player => Node => Seq[Any] = {
    case player: Player => {
      case node: Node => node match {
        case RolePlayingGraphRoot(players) => players.flatMap(p => kiama_getPredecessors(player)(p))
        case Player(core, role) if role == player.core => Seq(core) ++ getPredecessors(core)
        case _ => Seq.empty
      }
    }
  }

  private lazy val kiama_getPredecessors = paramAttr(kiama_getPredecessorsDef)

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): List[Any] =
    kiama_getPredecessors(Player(player, null))(root).distinct.toList

  private def kiama_merge(other: RoleGraph) {
    other.store.asInstanceOf[RolePlayingGraphRoot].players.foreach(pl => {
      root.players.contains(pl) match {
        case true => addBinding(pl.core.asInstanceOf[AnyRef], pl.role.asInstanceOf[AnyRef])
        case false => root.players += pl
      }
    })
  }

  override def merge(other: RoleGraph) {
    kiama_merge(other)
    resetAll()
  }

  private def kiama_removePlayer(player: Player) {
    root.players -= player
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P) {
    val p = Player(player, null)
    kiama_removePlayer(p)

    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()

    val key1 = new ParamAttributeKey(p, root)
    val key2 = new ParamAttributeKey(p, p)
    if (kiama_getRoles.hasBeenComputedAt(key1)) kiama_getRoles.resetAt(key1)
    if (kiama_getRoles.hasBeenComputedAt(key2)) kiama_getRoles.resetAt(key2)

    if (kiama_getPredecessors.hasBeenComputedAt(key1)) kiama_getPredecessors.resetAt(key1)
    if (kiama_getPredecessors.hasBeenComputedAt(key2)) kiama_getPredecessors.resetAt(key1)
  }

  private def kiama_removeBinding(player: Player) {
    root.players.find(p => p.core == player.core && p.role == player.role).foreach(root.players -= _)
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    kiama_removeBinding(Player(player, role))
    if (checkForCycles) kiama_hasCycle.reset()
    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()
    kiama_getRoles.reset()
    kiama_getPredecessors.reset()
  }

  override val store: DirectedGraph[Any, DefaultEdge] = null // we are not using it here!
}
