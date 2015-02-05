package internal.graph

import internal.graph.RoleGraph.{ RelationType, Relation }
import scalax.collection.constrained.constraints.{ Connected, Acyclic }
import scalax.collection.GraphEdge._
import scalax.collection.GraphPredef._

object RoleGraph {

  object RelationType extends Enumeration {
    type RelationType = Value
    val Plays, Fills = Value
  }

  import internal.graph.RoleGraph.RelationType._

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

class RoleGraph {
  implicit val config = Connected && Acyclic
  var store = scalax.collection.mutable.Graph[Any, Relation]()

  def addBinding(
    core: Any,
    role: Any): Unit =
    {
      val relA = core ~> role ## RelationType.Plays
      //val relB = role ~> core ## RelationType.Fills
      store += relA
      //store += relB
    }

  def removeBinding(
    core: Any,
    role: Any): Unit =
    {
      val relA = core ~> role ## RelationType.Plays
      //val relB = role ~> core ## RelationType.Fills
      store -= relA
      //store -= relB
    }

  def remove(player: Any): Unit =
    {
      store -= player
    }

  def getRoles(core: Any): Set[Any] = store.nodes.contains(core) match {
    case true => store.get(core).outerNodeTraverser.map(_.value).toSet
    case false => throw new RuntimeException("No roles for core " + core + ", this core was not found!")
  }
}