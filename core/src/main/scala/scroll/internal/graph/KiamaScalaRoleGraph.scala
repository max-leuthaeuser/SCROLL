package scroll.internal.graph

import org.jgrapht.DirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.kiama.attribution.ParamAttributeKey
import org.kiama.rewriting.{Rewriter, Strategy}
import org.kiama.util.TreeNode
import scroll.internal.support.DispatchQuery

import scala.collection.mutable
import scala.reflect.runtime.universe._
import org.kiama.attribution.Attribution._

object KiamaScalaRoleGraph {

  sealed trait Node extends TreeNode

  case class RolePlayingGraphRoot(var players: mutable.ListBuffer[Player]) extends Node

  case class Player(obj: Any, var roles: mutable.ListBuffer[Player] = mutable.ListBuffer.empty) extends Node {
    override def equals(other: scala.Any): Boolean = other match {
      case Player(o, _) => obj == o
      case _ => false
    }

    override def hashCode(): Int = obj.hashCode()
  }

}

class KiamaScalaRoleGraph extends Rewriter with RoleGraph {

  import KiamaScalaRoleGraph._

  private lazy val root = RolePlayingGraphRoot(mutable.ListBuffer.empty)

  private def resetAll() {
    Set(kiama_hasCycle, kiama_containsPlayer, kiama_allPlayers, kiama_getRoles, kiama_getPredecessors).foreach(_.reset())
  }

  private lazy val kiama_hasCycleDef: Node => Boolean = {
    case RolePlayingGraphRoot(players) =>
      players.exists(kiama_hasCycle)
    case p@Player(o, roles) =>
      kiama_getRoles(p)(p).contains(p) || roles.exists(kiama_hasCycle)
  }

  private lazy val kiama_hasCycle = attr(kiama_hasCycleDef)

  private def kiama_addBinding(player: Player, role: Player) {
    def addBindingRule(n: Node) {
      n match {
        case r: RolePlayingGraphRoot => kiama_allPlayers(r).find(_ == player) match {
          case Some(p) => addBindingRule(p)
          case None =>
            r.players += player
            addBindingRule(player)
        }
        case p: Player if p == player && !p.roles.contains(role) =>
          p.roles += role
        case p: Player => p.roles.foreach(addBindingRule(_))
      }
    }
    addBindingRule(root)
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    kiama_addBinding(Player(player), Player(role))
    kiama_hasCycle.reset()
    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()
    kiama_getRoles.reset()
    kiama_getPredecessors.reset()
    if (kiama_hasCycle(root)) throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
  }

  private lazy val kiama_getRolesDef: Player => Node => Seq[Player] = {
    case player: Player => {
      case node: Node => node match {
        case RolePlayingGraphRoot(players) =>
          players.find(_ == player) match {
            case Some(pl) => pl.roles ++ pl.roles.flatMap(r => kiama_getRoles(r)(r))
            case None => players.flatMap(pl => pl.roles.flatMap(kiama_getRoles(player)(_)))
          }
        case p@Player(_, roles) if p == player =>
          p.roles ++ p.roles.flatMap(r => kiama_getRoles(r)(r))
        case Player(_, roles) => roles.flatMap(r => kiama_getRoles(r)(r))
      }
    }
  }

  private lazy val kiama_getRoles = paramAttr(kiama_getRolesDef)

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] =
    containsPlayer(player) match {
      case true => kiama_getRoles(Player(player))(root).map(_.obj).toSet
      case false => Set(player)
    }

  private lazy val kiama_allPlayersDef: Node => Seq[Player] = {
    case RolePlayingGraphRoot(players) => players ++ players.flatMap(kiama_allPlayers(_))
    case Player(_, roles) => roles ++ roles.flatMap(kiama_allPlayers(_))
  }

  private lazy val kiama_allPlayers = attr(kiama_allPlayersDef)

  override def allPlayers: Seq[Any] = kiama_allPlayers(root).map(_.obj).distinct

  private lazy val kiama_containsPlayerDef: Player => Node => Boolean = {
    case player: Player => {
      case node: Node => node match {
        case RolePlayingGraphRoot(players) =>
          players.contains(player) match {
            case false => players.exists(kiama_containsPlayer(player)(_))
            case _ => true
          }
        case p@Player(_, roles) if p == player => true
        case p@Player(_, roles) => roles.contains(p) || roles.exists(kiama_containsPlayer(player)(_))
      }
    }
  }

  private lazy val kiama_containsPlayer = paramAttr(kiama_containsPlayerDef)

  override def containsPlayer(player: Any): Boolean = kiama_containsPlayer(Player(player))(root)

  private def kiama_detach(other: RoleGraph): RolePlayingGraphRoot = {
    lazy val detachRule: Strategy =
      rule[Node] {
        case r: RolePlayingGraphRoot =>
          other.allPlayers.foreach(pl =>
            other.getRoles(pl).foreach(rl =>
              removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
          r.players.filter(_.roles.isEmpty).foreach(pl => {
            r.players = kiama_removePlayer(pl).players
          })
          r
      }

    rewrite(topdown(attempt(detachRule)))(root)
  }

  override def detach(other: RoleGraph) {
    root.players = kiama_detach(other).players
    resetAll()
  }

  private lazy val kiama_getPredecessorsDef: Player => Node => Seq[Player] = {
    case player: Player => {
      case node: Node => node match {
        case RolePlayingGraphRoot(players) => players.contains(player) match {
          case true => Seq.empty
          case false => players.flatMap(kiama_getPredecessors(player)(_))
        }
        case p@Player(_, roles) => roles.contains(player) match {
          case true => Seq(p) ++ kiama_getPredecessors(p)(root)
          case false => roles.flatMap(kiama_getPredecessors(player)(_))
        }
      }
    }
  }

  private lazy val kiama_getPredecessors = paramAttr(kiama_getPredecessorsDef)

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): List[Any] =
    kiama_getPredecessors(Player(player))(root).distinct.toList.map(_.obj)

  private def kiama_merge(other: RoleGraph): RolePlayingGraphRoot = {
    val otherStore = other.store.asInstanceOf[RolePlayingGraphRoot]

    lazy val mergeRule: Strategy =
      rule[Node] {
        case r: RolePlayingGraphRoot =>
          otherStore.players.foreach(pl => {
            r.players.contains(pl) match {
              case true => pl.roles.foreach(rl => {
                kiama_addBinding(pl, rl)
              })
              case false => r.players += pl
            }
          })
          r
      }

    rewrite(topdown(attempt(mergeRule)))(root)
  }

  override def merge(other: RoleGraph) {
    root.players = kiama_merge(other).players
    resetAll()
  }

  private def kiama_removePlayer(player: Player): RolePlayingGraphRoot = {
    lazy val removePlayerRule: Strategy =
      rule[Node] {
        case g@RolePlayingGraphRoot(players) =>
          if (players.contains(player)) {
            g.players -= player
          }
          g
        case p@Player(_, roles) =>
          if (roles.contains(player)) {
            p.roles -= player
          }
          p
      }
    rewrite(topdown(attempt(removePlayerRule)))(root)
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P) {
    val p = Player(player)
    val newRoot = kiama_removePlayer(p)

    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()

    val key1 = new ParamAttributeKey(p, root)
    val key2 = new ParamAttributeKey(p, p)
    if (kiama_getRoles.hasBeenComputedAt(key1)) kiama_getRoles.resetAt(key1)
    if (kiama_getRoles.hasBeenComputedAt(key2)) kiama_getRoles.resetAt(key2)

    if (kiama_getPredecessors.hasBeenComputedAt(key1)) kiama_getPredecessors.resetAt(key1)
    if (kiama_getPredecessors.hasBeenComputedAt(key2)) kiama_getPredecessors.resetAt(key1)

    root.players = newRoot.players
  }

  private def kiama_removeBinding(player: Player, role: Player): RolePlayingGraphRoot = {
    lazy val removeBindingRule: Strategy =
      rule[Node] {
        case r@RolePlayingGraphRoot(players) =>
          if (players.contains(player) && player.roles.contains(role)) {
            player.roles -= role
          }
          r
        case p: Player if p == player && p.roles.contains(role) =>
          p.roles -= role
          p
        case p: Player if p == player && kiama_getRoles(p)(p).contains(role) =>
          val pred = kiama_getPredecessors(role)(p).head
          pred.roles -= role
          p
      }
    rewrite(topdown(attempt(removeBindingRule)))(root)
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    val newRoot = kiama_removeBinding(Player(player), Player(role))
    kiama_hasCycle.reset()
    kiama_containsPlayer.reset()
    kiama_allPlayers.reset()
    kiama_getRoles.reset()
    kiama_getPredecessors.reset()
    root.players = newRoot.players
  }

  override val store: DirectedGraph[Any, DefaultEdge] = null // we are not using it here!
}
