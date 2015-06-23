package internal.graph

import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.traverse.DepthFirstIterator
import scala.collection.JavaConversions._

class ScalaRoleGraph extends RoleGraph[Any] {
  // TODO: cycle detection
  var store = new DefaultDirectedGraph[Any, DefaultEdge](classOf[DefaultEdge])

  def merge(other: ScalaRoleGraph) {
    require(null != other)
    Graphs.addGraph(store, other.store)
  }

  def detach(other: ScalaRoleGraph) {
    require(null != other)
    store.removeAllEdges(other.store.edgeSet())
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

  /**
   * Returns a Seq of all players
   *
   * @return a Seq of all players
   */
  def allPlayers: Seq[Any] = store.vertexSet().toSeq

  def getPredecessors(player: Any): List[Any] = {
    val revGraph = new DefaultDirectedGraph[Any, DefaultEdge](classOf[DefaultEdge])
    Graphs.addGraphReversed(revGraph, store)
    new DepthFirstIterator[Any, DefaultEdge](revGraph, player).toList match {
      case Nil => List.empty
      case p :: Nil if p == player => List.empty
      case l => l.tail
    }
  }
}