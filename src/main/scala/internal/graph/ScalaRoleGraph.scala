package internal.graph

import internal.graph.ScalaRoleGraph.{ RelationType, Relation }
import scalax.collection.constrained.constraints.{ Connected, Acyclic }
import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._

object ScalaRoleGraph {
  object RelationType extends Enumeration {
    type RelationType = Value
    val Plays, Fills = Value
  }

  import internal.graph.ScalaRoleGraph.RelationType._

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

  override def addBinding(core: Any, role: Any) {
    val relA = core ~> role ## RelationType.Plays
    store += relA
  }

  override def removeBinding(core: Any, role: Any) {
    val relA = core ~> role ## RelationType.Plays
    store -= relA
  }

  override def removePlayer(player: Any) {
    store -= player
  }

  override def getRoles(core: Any): Set[Any] = store.nodes.contains(core) match {
    case true => store.get(core).outerNodeTraverser.map(_.value).toSet
    case false => throw new RuntimeException(s"No roles for core '$core' this core was not found!")
  }
}