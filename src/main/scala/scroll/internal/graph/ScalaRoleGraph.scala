package scroll.internal.graph

import org.jgrapht.Graphs
import org.jgrapht.experimental.dag.DirectedAcyclicGraph
import org.jgrapht.graph.{EdgeReversedGraph, DefaultEdge}
import org.jgrapht.traverse.DepthFirstIterator
import scala.collection.JavaConversions._

class ScalaRoleGraph extends RoleGraph[Any] {
  override val store = new DirectedAcyclicGraph[Any, DefaultEdge](classOf[DefaultEdge])

  override def merge(other: RoleGraph[Any]) {
    require(null != other)
    Graphs.addGraph(store, other.store)
  }

  override def detach(other: RoleGraph[Any]) {
    require(null != other)
    store.removeAllVertices(other.store.vertexSet())
  }

  override def addBinding(player: Any, role: Any) {
    require(null != player)
    require(null != role)
    store.addVertex(player)
    store.addVertex(role)
    store.addEdge(player, role)
  }

  override def removeBinding(player: Any, role: Any) {
    require(null != player)
    require(null != role)
    store.removeEdge(player, role)
  }

  override def removePlayer(player: Any) {
    require(null != player)
    store.removeVertex(player)
  }

  override def getRoles(player: Any): Set[Any] = {
    require(null != player)
    containsPlayer(player) match {
      case true => new DepthFirstIterator[Any, DefaultEdge](store, player).toSet
      case false => Set(player)
    }
  }

  override def containsPlayer(player: Any): Boolean = store.containsVertex(player)

  override def allPlayers: Seq[Any] = store.vertexSet().toSeq

  override def getPredecessors(player: Any): List[Any] =
    new DepthFirstIterator[Any, DefaultEdge](new EdgeReversedGraph[Any, DefaultEdge](store), player).toList match {
      case Nil => List.empty
      case p :: Nil if p == player => List.empty
      case l => l.tail
    }
}