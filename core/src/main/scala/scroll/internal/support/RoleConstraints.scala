package scroll.internal.support

import org.jgrapht.graph.{DefaultDirectedGraph, DefaultEdge}
import org.jgrapht.traverse.DepthFirstIterator
import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.collection.JavaConversions._
import scala.reflect.Manifest

trait RoleConstraints {
  self: Compartment =>

  private lazy val roleImplications = newRoleConstraintGraph
  private lazy val roleEquivalents = newRoleConstraintGraph
  private lazy val roleProhibitions = newRoleConstraintGraph

  private def isInstanceOf(mani: String, that: Any) =
    ReflectiveHelper.classSimpleClassName(that.getClass.toString) == ReflectiveHelper.classSimpleClassName(mani)

  private def newRoleConstraintGraph = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

  private def checkImplications(player: Any, role: Any) {
    val candidates = roleImplications.vertexSet().filter(isInstanceOf(_, role))
    candidates.isEmpty match {
      case false =>
        val allImplicitRoles = candidates.flatMap(new DepthFirstIterator[String, DefaultEdge](roleImplications, _).toSet)
        val allRoles = plays.getRoles(player).diff(Set(player))
        allImplicitRoles.foreach(r => if (!allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role implication constraint violation: '$player' should play role '$r', but it does not!")
        })
      case true => //done, thanks
    }
  }

  private def checkEquivalence(player: Any, role: Any) {
    val candidates = roleEquivalents.vertexSet().filter(isInstanceOf(_, role))
    candidates.isEmpty match {
      case false =>
        val allEquivalentRoles = candidates.flatMap(new DepthFirstIterator[String, DefaultEdge](roleEquivalents, _).toSet)
        val allRoles = plays.getRoles(player).diff(Set(player))
        allEquivalentRoles.foreach(r => if (!allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role equivalence constraint violation: '$player' should play role '$r', but it does not!")
        })
      case true => //done, thanks
    }
  }

  private def checkProhibitions(player: Any, role: Any) {
    val candidates = roleProhibitions.vertexSet().filter(isInstanceOf(_, role))
    candidates.isEmpty match {
      case false =>
        val allProhibitedRoles = candidates.flatMap(new DepthFirstIterator[String, DefaultEdge](roleProhibitions, _).toSet)
        val allRoles = plays.getRoles(player).diff(Set(player))
        val rs = allProhibitedRoles.size == allRoles.size match {
          case false => allProhibitedRoles.filter(r => allRoles.exists(isInstanceOf(r, _)))
          case true => Set[String]()
        }
        allProhibitedRoles.diff(rs).diff(candidates).foreach(r => if (allRoles.exists(isInstanceOf(r, _))) {
          throw new RuntimeException(s"Role prohibition constraint violation: '$player' plays role '$r', but it is not allowed to do so!")
        })
      case true => //done, thanks
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
  def RoleImplication[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleImplications.addVertex(rA)
    roleImplications.addVertex(rB)
    roleImplications.addEdge(rA, rB)
  }

  /**
   * Adds an role equivalent constraint between the given role types.
   * Interpretation: if a core object plays an instance of role type A
   * it also has to play an instance of role type B and visa versa.
   *
   * @tparam A type of role A that should be played implicitly if B is played
   * @tparam B type of role B that should be played implicitly if A is played
   */
  def RoleEquivalence[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleEquivalents.addVertex(rA)
    roleEquivalents.addVertex(rB)
    roleEquivalents.addEdge(rA, rB)
    roleEquivalents.addEdge(rB, rA)
  }

  /**
   * Adds an role prohibition constraint between the given role types.
   * Interpretation: if a core object plays an instance of role type A
   * it is not allowed to play B as well.
   *
   * @tparam A type of role A
   * @tparam B type of role B that is not allowed to be played if A is played already
   */
  def RoleProhibition[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleProhibitions.addVertex(rA)
    roleProhibitions.addVertex(rB)
    roleProhibitions.addEdge(rA, rB)
  }

  /**
   * Wrapping function that checks all available role constraints for
   * all core objects and its roles after the given function was executed.
   * Throws a RuntimeException if a role constraint is violated!
   *
   * @param func the function to execute and check role constraints afterwards
   */
  def RoleConstraintsChecked(func: => Unit) {
    func
    plays.allPlayers.foreach(p => plays.getRoles(p).diff(Set(p)).foreach(r => validate(p, r)))
  }

  /**
   * Checks all role constraints between the given player and role instance.
   * Will throw a RuntimeException if a constraint is violated!
   *
   * @param player the player instance to check
   * @param role the role instance to check
   */
  def validate(player: Any, role: Any) {
    checkImplications(player, role)
    checkEquivalence(player, role)
    checkProhibitions(player, role)
  }
}
