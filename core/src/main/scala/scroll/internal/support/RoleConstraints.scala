package scroll.internal.support

import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.reflect.{ClassTag, classTag}
import com.google.common.graph.{GraphBuilder, Graphs, MutableGraph}

import scala.collection.JavaConverters._

/**
  * Allows to add and check role constraints (Riehle constraints) to a compartment instance.
  */
trait RoleConstraints {
  self: Compartment =>

  protected val roleImplications: MutableGraph[String] = GraphBuilder.directed().build[String]()
  protected val roleEquivalents: MutableGraph[String] = GraphBuilder.directed().build[String]()
  protected val roleProhibitions: MutableGraph[String] = GraphBuilder.directed().build[String]()

  private def isInstanceOf(mani: String, that: Any) =
    ReflectiveHelper.simpleName(that.getClass.toString) == ReflectiveHelper.simpleName(mani)

  private def checkImplications(player: Any, role: Any): Unit = {
    roleImplications.nodes().asScala.filter(isInstanceOf(_, role)).toList match {
      case Nil => //done, thanks
      case list =>
        val allImplicitRoles = list.flatMap(Graphs.reachableNodes(roleImplications, _).asScala)
        val allRoles = plays.getRoles(player).diff(Seq(player))
        allImplicitRoles.foreach(r => if (!allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role implication constraint violation: '$player' should play role '$r', but it does not!")
        })
    }
  }

  private def checkEquivalence(player: Any, role: Any): Unit = {
    roleEquivalents.nodes().asScala.filter(isInstanceOf(_, role)).toList match {
      case Nil => //done, thanks
      case list =>
        val allEquivalentRoles = list.flatMap(Graphs.reachableNodes(roleEquivalents, _).asScala)
        val allRoles = plays.getRoles(player).diff(Seq(player))
        allEquivalentRoles.foreach(r => if (!allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role equivalence constraint violation: '$player' should play role '$r', but it does not!")
        })
    }
  }

  private def checkProhibitions(player: Any, role: Any): Unit = {
    roleProhibitions.nodes().asScala.filter(isInstanceOf(_, role)).toList match {
      case Nil => //done, thanks
      case list =>
        val allProhibitedRoles = list.flatMap(Graphs.reachableNodes(roleProhibitions, _).asScala).toSet
        val allRoles = plays.getRoles(player).diff(Seq(player))
        val rs = if (allProhibitedRoles.size == allRoles.size) {
          Set.empty[String]
        } else {
          allProhibitedRoles.filter(r => allRoles.exists(isInstanceOf(r, _)))
        }
        allProhibitedRoles.diff(rs).diff(list.toSet).foreach(r => if (allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role prohibition constraint violation: '$player' plays role '$r', but it is not allowed to do so!")
        })
    }
  }

  /**
    * Adds an role implication constraint between the given role types.
    * Interpretation: if a core object plays an instance of role type A
    * it also has to play an instance of role type B.
    *
    * @tparam A type of role A
    * @tparam B type of role B that should be played implicitly if A is played
    */
  def RoleImplication[A: ClassTag, B: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _ = roleImplications.putEdge(rA, rB)
  }

  /**
    * Adds an role equivalent constraint between the given role types.
    * Interpretation: if a core object plays an instance of role type A
    * it also has to play an instance of role type B and visa versa.
    *
    * @tparam A type of role A that should be played implicitly if B is played
    * @tparam B type of role B that should be played implicitly if A is played
    */
  def RoleEquivalence[A: ClassTag, B: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _ = (roleEquivalents.putEdge(rA, rB), roleEquivalents.putEdge(rB, rA))
  }

  /**
    * Adds an role prohibition constraint between the given role types.
    * Interpretation: if a core object plays an instance of role type A
    * it is not allowed to play B as well.
    *
    * @tparam A type of role A
    * @tparam B type of role B that is not allowed to be played if A is played already
    */
  def RoleProhibition[A: ClassTag, B: ClassTag](): Unit = {
    val rA = classTag[A].toString
    val rB = classTag[B].toString
    val _ = roleProhibitions.putEdge(rA, rB)
  }

  /**
    * Wrapping function that checks all available role constraints for
    * all core objects and its roles after the given function was executed.
    * Throws a RuntimeException if a role constraint is violated!
    *
    * @param func the function to execute and check role constraints afterwards
    */
  def RoleConstraintsChecked(func: => Unit): Unit = {
    func
    plays.allPlayers.foreach(p => plays.getRoles(p).diff(Seq(p)).foreach(r => validateConstraints(p, r)))
  }

  /**
    * Checks all role constraints between the given player and role instance.
    * Will throw a RuntimeException if a constraint is violated!
    *
    * @param player the player instance to check
    * @param role   the role instance to check
    */
  private def validateConstraints(player: Any, role: Any): Unit = {
    checkImplications(player, role)
    checkEquivalence(player, role)
    checkProhibitions(player, role)
  }
}
