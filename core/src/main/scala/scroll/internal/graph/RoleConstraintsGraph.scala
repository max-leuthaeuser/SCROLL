package scroll.internal.graph

import org.jgrapht.graph.{DefaultDirectedGraph, DefaultEdge}
import org.jgrapht.traverse.DepthFirstIterator
import scroll.internal.ReflectiveHelper

import scala.collection.JavaConversions._
import scala.reflect.Manifest

class RoleConstraintsGraph(private val forGraph: RoleGraph[Any]) {
  private lazy val roleImplications = newRoleConstraintGraph
  private lazy val roleEquivalents = newRoleConstraintGraph
  private lazy val roleProhibitions = newRoleConstraintGraph

  private def isInstanceOf(mani: String, that: Any) =
    ReflectiveHelper.classSimpleClassName(that.getClass.toString) == ReflectiveHelper.classSimpleClassName(mani)

  private def newRoleConstraintGraph = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

  def addImplication[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleImplications.addVertex(rA)
    roleImplications.addVertex(rB)
    roleImplications.addEdge(rA, rB)
  }

  def addEquivalence[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleEquivalents.addVertex(rA)
    roleEquivalents.addVertex(rB)
    roleEquivalents.addEdge(rA, rB)
    roleEquivalents.addEdge(rB, rA)
  }

  def addProhibition[A: Manifest, B: Manifest]() {
    val rA = manifest[A].toString()
    val rB = manifest[B].toString()
    roleProhibitions.addVertex(rA)
    roleProhibitions.addVertex(rB)
    roleProhibitions.addEdge(rA, rB)
  }

  private def checkImplications(player: Any, role: Any) {
    val candidates = roleImplications.vertexSet().filter(isInstanceOf(_, role))
    candidates.isEmpty match {
      case false =>
        val allImplicitRoles = candidates.flatMap(new DepthFirstIterator[String, DefaultEdge](roleImplications, _).toSet)
        val allRoles = forGraph.getRoles(player).diff(Set(player))
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
        val allRoles = forGraph.getRoles(player).diff(Set(player))
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
        val allRoles = forGraph.getRoles(player).diff(Set(player))
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
