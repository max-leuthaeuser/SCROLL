package internal.graph

import internal.graph.ScalaRoleGraph.{RelationType, Relation}
import scalax.collection.constrained.constraints.{Connected, Acyclic}
import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._
import scalax.collection.mutable.Graph

object ScalaRoleGraph {

  object RelationType extends Enumeration {
    type RelationType = Value
    val Plays = Value
  }

  import RelationType._

  class Relation[N](
                     nodes: Product,
                     val rtype: RelationType)
    extends DiEdge[N](nodes)
    with ExtendedKey[N]
    with EdgeCopy[Relation]
    with OuterEdge[N, Relation] {

    def keyAttributes = Seq(rtype)

    override def copy[NN](newNodes: Product) =
      new Relation[NN](newNodes, rtype)
  }

  object Relation {
    def apply(
               from: Any,
               to: Any,
               t: RelationType) =
      new Relation[Any](NodeProduct(from, to), t)

    def unapply(e: Relation[Any]): Option[(Any, Any, RelationType)] =
      if (e eq null) None else Some(e.from, e.to, e.rtype)
  }

  implicit class RelationAssoc[A <: Any](val e: DiEdge[A]) {
    @inline def ##(rel: RelationType) =
      new Relation[A](e.nodes, rel) with OuterEdge[A, Relation]
  }

}

class ScalaRoleGraph extends RoleGraph[Any] {
  implicit val config = Connected && Acyclic
  var store = scalax.collection.mutable.Graph[Any, Relation]()

  def merge(other: ScalaRoleGraph) {
    require(null != other)
    store ++= other.store
  }

  def detach(other: ScalaRoleGraph) {
    require(null != other)
    store --= other.store
  }

  override def addBinding(player: Any, role: Any) {
    require(null != player)
    require(null != role)
    val relA = player ~> role ## RelationType.Plays
    store += relA
  }

  override def removeBinding(player: Any, role: Any) {
    require(null != player)
    require(null != role)
    val relA = player ~> role ## RelationType.Plays
    store -= relA
  }

  override def removePlayer(player: Any) {
    require(null != player)
    store -= player
  }

  override def getRoles(player: Any): Set[Any] = {
    require(null != player)
    contains(player) match {
      case true => store.get(player).outerNodeTraverser.map(_.value).toSet
      case false => Set(player)
    }
  }

  override def contains(player: Any): Boolean = store.contains(player)

  /**
   * Returns a Seq of all players
   *
   * @return a Seq of all players
   */
  def allPlayers: Seq[Graph[Any, Relation]#NodeT] = store.nodes.toSeq

  /**
   * Returns the NodeT instance for the given player instance.
   *
   * @param player the player instance to look up.
   * @return the NodeT instance for the given player instance
   */
  def get(player: Any): Graph[Any, Relation]#NodeT = store.get(player)
}