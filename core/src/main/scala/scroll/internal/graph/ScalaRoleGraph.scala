package scroll.internal.graph

import org.jgrapht.Graphs
import org.jgrapht.experimental.dag.DirectedAcyclicGraph
import org.jgrapht.graph.{DefaultEdge, EdgeReversedGraph}
import org.jgrapht.traverse.DepthFirstIterator

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * JGraphTs [[org.jgrapht.experimental.dag.DirectedAcyclicGraph]] as underlying data model.
  */
class ScalaRoleGraph extends RoleGraph {
  override lazy val store = new DirectedAcyclicGraph[Any, DefaultEdge](classOf[DefaultEdge])

  override def merge(other: RoleGraph) {
    require(null != other)
    Graphs.addGraph(store, other.store)
  }

  override def detach(other: RoleGraph) {
    require(null != other)
    store.removeAllVertices(other.store.vertexSet())
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    require(null != player)
    require(null != role)
    store.addVertex(player)
    store.addVertex(role)
    store.addEdge(player, role)
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R) {
    require(null != player)
    require(null != role)
    store.removeEdge(player, role)
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P) {
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