package scroll.internal.graph

import com.google.common.graph.{ValueGraphBuilder, Graphs}
import scroll.internal.support.DispatchQuery

import scala.reflect.ClassTag
import collection.JavaConverters._
import scala.collection.mutable

/**
  * Scala specific implementation of a [[scroll.internal.graph.RoleGraph]] using
  * a graph as underlying data model.
  *
  * @param checkForCycles set to true to forbid cyclic role playing relationships
  */
class ScalaRoleGraph(checkForCycles: Boolean = true) extends RoleGraph {

  import scroll.internal.MetaType._

  private var root = ValueGraphBuilder.directed().build[Object, MetaType]()

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
      source.edges().forEach(p => {
        val _ = target.putEdgeValue(p.source(), p.target(), source.edgeValueOrDefault(p.source(), p.target(), Role))
      })
      root = target
    } else {
      target.edges().forEach(p => {
        val _ = root.putEdgeValue(p.source(), p.target(), target.edgeValueOrDefault(p.source(), p.target(), Role))
      })
    }
    checkCycles()
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl, rl)))
  }

  private def checkCycles(): Unit = {
    if (checkForCycles) {
      if (Graphs.hasCycle(root.asGraph())) {
        throw new RuntimeException(s"Cyclic role-playing relationship found!")
      }
    }
  }

  override def addBinding[P <: AnyRef : ClassTag, R <: AnyRef : ClassTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)

    role match {
      case _: Enumeration#Value => root.putEdgeValue(player, role, Facet)
      case _ => root.putEdgeValue(player, role, Role)
    }

    if (checkForCycles && Graphs.hasCycle(root.asGraph())) {
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

  override def getRoles(player: AnyRef)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[AnyRef] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Object]
      val processing = new mutable.Queue[Object]
      returnSeq += player.asInstanceOf[Object]
      root.successors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      while (processing.nonEmpty) {
        val next = processing.dequeue()
        if (!returnSeq.contains(next))
          returnSeq += next
        root.successors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      }
      returnSeq
    }
    else {
      Seq.empty
    }
  }

  override def getFacets(player: AnyRef)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[Enumeration#Value] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Enumeration#Value]
      root.successors(player.asInstanceOf[Object]).forEach {
        case e: Enumeration#Value => returnSeq += e
        case _ =>
      }
      returnSeq
    }
    else {
      Seq.empty
    }
  }

  override def containsPlayer(player: AnyRef): Boolean = root.nodes().contains(player)

  override def allPlayers: Seq[AnyRef] = root.nodes().asScala.toSeq

  override def getPredecessors(player: AnyRef)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Seq[AnyRef] = {
    val returnSeq = new mutable.ListBuffer[Object]
    val processing = new mutable.Queue[Object]
    root.predecessors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
    while (processing.nonEmpty) {
      val next = processing.dequeue()
      if (!returnSeq.contains(next))
        returnSeq += next
      root.predecessors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
    }
    returnSeq
  }
}