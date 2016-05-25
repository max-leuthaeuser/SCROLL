package scroll.internal.graph

import org.bitbucket.inkytonik.kiama.attribution.{Attribution, ParamAttributeKey}
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultEdge
import scroll.internal.support.DispatchQuery

import scala.collection.mutable
import scala.reflect.runtime.universe._

object KiamaScalaRoleGraph {

  sealed trait Node

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

class KiamaScalaRoleGraph(checkForCycles: Boolean = true) extends Attribution with RoleGraph {

  import KiamaScalaRoleGraph._

  private val empty = Seq.empty[Any]

  private val root = RolePlayingGraphRoot(mutable.ListBuffer.empty)

  private def resetAll(): Unit = {
    Set(kiama_hasCycle, kiama_containsPlayer, kiama_allPlayers, kiama_getRoles, kiama_getPredecessors).foreach(_.reset())
  }

  private lazy val kiama_hasCycleDef: Node => Boolean = {
    case RolePlayingGraphRoot(players) => players.exists(kiama_hasCycle)
    case p@Player(core, _) => getRoles(p.core).contains(p.core)
    case _ => false
  }

  private lazy val kiama_hasCycle = attr(kiama_hasCycleDef)

  private def kiama_addBinding(player: Player): Unit = {
    val _ = root.players += player
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
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
        case r: RolePlayingGraphRoot => r.players.flatMap(p => kiama_getRoles(player)(p))
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
        case _ => empty
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

  private def kiama_detach(other: RoleGraph): Unit = {
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
    root.players.foreach(kiama_removePlayer)
  }

  override def detach(other: RoleGraph): Unit = {
    kiama_detach(other)
    resetAll()
  }

  private lazy val kiama_getPredecessorsDef: Player => Node => Seq[Any] = {
    case player: Player => {
      case node: Node => node match {
        case RolePlayingGraphRoot(players) => players.flatMap(p => kiama_getPredecessors(player)(p))
        case Player(core, role) if role == player.core => Seq(core) ++ getPredecessors(core)
        case _ => empty
      }
    }
  }

  private lazy val kiama_getPredecessors = paramAttr(kiama_getPredecessorsDef)

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): List[Any] =
    kiama_getPredecessors(Player(player, null))(root).distinct.toList

  private def kiama_merge(other: RoleGraph): Unit = {
    other.store.asInstanceOf[RolePlayingGraphRoot].players.foreach(pl => {
      root.players.contains(pl) match {
        case true => addBinding(pl.core.asInstanceOf[AnyRef], pl.role.asInstanceOf[AnyRef])
        case false => root.players += pl
      }
    })
  }

  override def merge(other: RoleGraph): Unit = {
    kiama_merge(other)
    resetAll()
  }

  private def kiama_removePlayer(player: Player): Unit = {
    val _ = root.players -= player
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P): Unit = {
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

  private def kiama_removeBinding(player: Player): Unit = {
    root.players.find(p => p.core == player.core && p.role == player.role).foreach(root.players -= _)
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    kiama_removeBinding(Player(player, role))
    if (checkForCycles) kiama_hasCycle.reset()
    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()
    kiama_getRoles.reset()
    kiama_getPredecessors.reset()
  }

  override val store: DirectedGraph[Any, DefaultEdge] = null // we are not using it here!
}
