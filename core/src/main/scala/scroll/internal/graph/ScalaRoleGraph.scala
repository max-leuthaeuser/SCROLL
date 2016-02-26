package scroll.internal.graph

import org.jgrapht.Graphs
import org.jgrapht.experimental.dag.DirectedAcyclicGraph
import org.jgrapht.graph.{SimpleDirectedGraph, DefaultEdge, EdgeReversedGraph}
import org.jgrapht.traverse.{BreadthFirstIterator, DepthFirstIterator}
import scroll.internal.support.DispatchQuery

import scala.collection.JavaConversions._
import scala.reflect.runtime.universe._

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * JGraphTs [[org.jgrapht.DirectedGraph]] as underlying data model.
  *
  * @param checkForCycles set to true to use an [[org.jgrapht.experimental.dag.DirectedAcyclicGraph]] as underlying
  *                       data model, or to false to use [[org.jgrapht.graph.SimpleDirectedGraph]]
  */
class ScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {
  override val store = checkForCycles match {
    case true => new DirectedAcyclicGraph[Any, DefaultEdge](classOf[DefaultEdge])
    case false => new SimpleDirectedGraph[Any, DefaultEdge](classOf[DefaultEdge])
  }

  override def merge(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    val _ = Graphs.addGraph(store, other.store)
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only detach RoleGraphs of the same type!")
    val _ = store.removeAllVertices(other.store.vertexSet())
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    store.addVertex(player)
    store.addVertex(role)
    val _ = store.addEdge(player, role)
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    val _ = store.removeEdge(player, role)
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P): Unit = {
    require(null != player)
    val _ = store.removeVertex(player)
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] = {
    require(null != player)
    val r = containsPlayer(player) match {
      case true => dispatchQuery.traversalStrategy match {
        case DispatchQuery.DFS => new DepthFirstIterator[Any, DefaultEdge](store, player).toSet
        case DispatchQuery.BFS => new BreadthFirstIterator[Any, DefaultEdge](store, player).toSet
      }
      case false => Set(player)
    }
    r.diff(Set(player))
  }

  override def containsPlayer(player: Any): Boolean = store.containsVertex(player)

  override def allPlayers: Seq[Any] = store.vertexSet().toSeq

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): List[Any] = {
    val it = dispatchQuery.traversalStrategy match {
      case DispatchQuery.DFS => new DepthFirstIterator[Any, DefaultEdge](new EdgeReversedGraph[Any, DefaultEdge](store), player).toList
      case DispatchQuery.BFS => new BreadthFirstIterator[Any, DefaultEdge](new EdgeReversedGraph[Any, DefaultEdge](store), player).toList
    }
    it match {
      case Nil => List.empty
      case p :: Nil if p == player => List.empty
      case l => l.tail
    }
  }
}