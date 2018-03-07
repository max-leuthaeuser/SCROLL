package scroll.internal.graph

import com.google.common.graph.{GraphBuilder, Graphs, MutableGraph}

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

  private var root: MutableGraph[Object] = GraphBuilder.directed().build[Object]()

  private def checkAndMerge(source: MutableGraph[Object], target: MutableGraph[Object]): Unit =
    (source.nodes().size, target.nodes().size) match {
      case (_, t) if t == 0 => //do nothing; source is correct
      case (s, _) if s == 0 =>
        //take target because source is empty:
        root = target
        checkCycles()
      case (s, t) if s < t =>
        source.edges().forEach(p => {
          val _ = target.putEdge(p.source(), p.target())
        })
        root = target
        checkCycles()
      case _ =>
        // default case:
        target.edges().forEach(p => {
          val _ = root.putEdge(p.source(), p.target())
        })
        checkCycles()
    }

  override def merge(other: RoleGraph): Unit = {
    require(null != other)
    require(other.isInstanceOf[ScalaRoleGraph], "You can only merge RoleGraphs of the same type!")
    checkAndMerge(root, other.asInstanceOf[ScalaRoleGraph].root)
  }

  override def detach(other: RoleGraph): Unit = {
    require(null != other)
    other.allPlayers.foreach(pl =>
      other.getRoles(pl).foreach(rl =>
        removeBinding(pl, rl)))
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

  override def getRoles(player: AnyRef): Seq[AnyRef] = {
    require(null != player)
    if (containsPlayer(player)) {
      val returnSeq = new mutable.ListBuffer[Object]
      val processing = new mutable.Queue[Object]
      returnSeq += player.asInstanceOf[Object]
      root.successors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      while (processing.nonEmpty) {
        val next = processing.dequeue()
        if (!returnSeq.contains(next)) {
          returnSeq += next
        }
        root.successors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
      }
      returnSeq
    }
    else {
      Seq.empty
    }
  }

  override def getFacets(player: AnyRef): Seq[Enumeration#Value] = {
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

  override def getPredecessors(player: AnyRef): Seq[AnyRef] = {
    val returnSeq = new mutable.ListBuffer[Object]
    val processing = new mutable.Queue[Object]
    root.predecessors(player.asInstanceOf[Object]).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
    while (processing.nonEmpty) {
      val next = processing.dequeue()
      if (!returnSeq.contains(next)) {
        returnSeq += next
      }
      root.predecessors(next).forEach(n => if (!n.isInstanceOf[Enumeration#Value]) processing.enqueue(n))
    }
    returnSeq
  }
}