package scroll.internal.graph

import com.google.common.graph.{GraphBuilder, Graphs}
import scroll.internal.support.DispatchQuery

import scala.reflect.ClassTag
import collection.JavaConverters._

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a graph as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class ScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

  private var root = GraphBuilder.directed().build[Object]()

  override def merge(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only merge RoleGraphs of the same type!")

    val source = root
    val target = other.asInstanceOf[ScalaRoleGraph].root

    if (source.nodes().isEmpty && target.nodes().isEmpty) return

    if (source.nodes().isEmpty && !target.nodes().isEmpty) {
      root = target
      checkCycles()
      return
    }

    if (!source.nodes().isEmpty && target.nodes().isEmpty) return

    if (source.nodes().size < target.nodes().size) {
      source.edges().asScala.foreach(p => target.putEdge(p.source(), p.target()))
      root = target
    } else {
      target.edges().asScala.foreach(p => root.putEdge(p.source(), p.target()))
    }
    checkCycles()
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl.asInstanceOf[AnyRef], rl.asInstanceOf[AnyRef])))
  }

  private def checkCycles(): Unit = {
    if (checkForCycles) {
      if (Graphs.hasCycle(root)) {
        throw new RuntimeException(s"Cyclic role-playing relationship found!")
      }
    }
  }

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    root.putEdge(player, role)
    if (checkForCycles && Graphs.hasCycle(root)) {
      throw new RuntimeException(s"Cyclic role-playing relationship for player '$player' found!")
    }
  }

  override def removeBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    val _ = root.removeEdge(player, role)
  }

  override def removePlayer[P <: AnyRef : ClassTag](player: P): Unit = {
    require(null != player)
    val _ = root.removeNode(player)
  }

  override def getRoles(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Set[Any] = {
    require(null != player)
    Graphs.reachableNodes(root, player).asScala.toSet
  }

  override def containsPlayer(player: Any): Boolean = root.nodes().contains(player)

  override def allPlayers: Seq[Any] = root.nodes().asScala.toSeq

  override def getPredecessors(player: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[Any] =
    Graphs.reachableNodes(Graphs.transpose(root), player).asScala.toSeq.tail
}