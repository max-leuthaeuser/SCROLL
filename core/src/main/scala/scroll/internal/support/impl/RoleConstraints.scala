package scroll.internal.support.impl

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Graphs
import com.google.common.graph.MutableGraph
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.RoleConstraintsApi
import scroll.internal.util.ReflectiveHelper

import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag
import scala.reflect.classTag

class RoleConstraints(private[this] val roleGraph: RoleGraphProxyApi) extends RoleConstraintsApi {

  private[this] lazy val roleImplications: MutableGraph[String] =
    GraphBuilder.directed().build[String]()

  private[this] lazy val roleEquivalents: MutableGraph[String] =
    GraphBuilder.directed().build[String]()

  private[this] lazy val roleProhibitions: MutableGraph[String] =
    GraphBuilder.directed().build[String]()

  private[this] def checkImplications(player: AnyRef, role: AnyRef): Unit = {
    val list = roleImplications.nodes().asScala.filter(ReflectiveHelper.isInstanceOf(_, role))
    if (list.nonEmpty) {
      val allImplicitRoles = list.flatMap(Graphs.reachableNodes(roleImplications, _).asScala)
      val allRoles         = roleGraph.plays.roles(player)
      allImplicitRoles.foreach(r =>
        if (!allRoles.exists(ReflectiveHelper.isInstanceOf(r, _))) {
          throw new RuntimeException(
            s"Role implication constraint violation: '$player' should play role '$r', but it does not!"
          )
        }
      )
    }
  }

  private[this] def checkEquivalence(player: AnyRef, role: AnyRef): Unit = {
    val list = roleEquivalents.nodes().asScala.filter(ReflectiveHelper.isInstanceOf(_, role))
    if (list.nonEmpty) {
      val allEquivalentRoles = list.flatMap(Graphs.reachableNodes(roleEquivalents, _).asScala)
      val allRoles           = roleGraph.plays.roles(player)
      allEquivalentRoles.foreach(r =>
        if (!allRoles.exists(ReflectiveHelper.isInstanceOf(r, _))) {
          throw new RuntimeException(
            s"Role equivalence constraint violation: '$player' should play role '$r', but it does not!"
          )
        }
      )
    }
  }

  private[this] def checkProhibitions(player: AnyRef, role: AnyRef): Unit = {
    val list = roleProhibitions.nodes().asScala.filter(ReflectiveHelper.isInstanceOf(_, role))
    if (list.nonEmpty) {
      val allProhibitedRoles =
        list.flatMap(Graphs.reachableNodes(roleProhibitions, _).asScala).toSet
      val allRoles = roleGraph.plays.roles(player)
      val rs = if (allProhibitedRoles.size == allRoles.size) {
        Set.empty[String]
      } else {
        allProhibitedRoles.filter(r => allRoles.exists(ReflectiveHelper.isInstanceOf(r, _)))
      }
      allProhibitedRoles
        .diff(rs)
        .diff(list.toSet)
        .foreach(r =>
          if (allRoles.exists(ReflectiveHelper.isInstanceOf(r, _))) {
            throw new RuntimeException(
              s"Role prohibition constraint violation: '$player' plays role '$r', but it is not allowed to do so!"
            )
          }
        )
    }
  }

  override def addRoleImplication[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _  = roleImplications.putEdge(rA, rB)
  }

  override def addRoleEquivalence[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _  = (roleEquivalents.putEdge(rA, rB), roleEquivalents.putEdge(rB, rA))
  }

  override def addRoleProhibition[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _  = roleProhibitions.putEdge(rA, rB)
  }

  override def checked(func: => Unit): Unit = {
    func
    roleGraph.plays.allPlayers.foreach(p =>
      roleGraph.plays.roles(p).foreach(r => validateConstraints(p, r))
    )
  }

  /** Checks all role constraints between the given player and role instance. Will throw a
    * RuntimeException if a constraint is violated!
    *
    * @param player
    *   the player instance to check
    * @param role
    *   the role instance to check
    */
  private[this] def validateConstraints(player: AnyRef, role: AnyRef): Unit = {
    checkImplications(player, role)
    checkEquivalence(player, role)
    checkProhibitions(player, role)
  }

}
