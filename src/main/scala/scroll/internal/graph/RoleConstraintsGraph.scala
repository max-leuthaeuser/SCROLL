package scroll.internal.graph

import org.jgrapht.graph.{DefaultEdge, DefaultDirectedGraph}
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

  private def checkImplications(player: Any, role: Any): Boolean = {
    val candidates = roleImplications.vertexSet().filter(isInstanceOf(_, role))
    if (candidates.isEmpty) return true

    val allImplicitRoles = candidates.map(new DepthFirstIterator[String, DefaultEdge](roleImplications, _).toSet).flatten
    val allRoles = forGraph.getRoles(player).diff(Set(player))

    allImplicitRoles.forall(r => allRoles.exists(isInstanceOf(r, _)))
  }

  private def checkEquivalence(player: Any, role: Any): Boolean = {
    val candidates = roleEquivalents.vertexSet().filter(isInstanceOf(_, role))
    if (candidates.isEmpty) return true

    val allEquivalentRoles = candidates.map(new DepthFirstIterator[String, DefaultEdge](roleEquivalents, _).toSet).flatten
    val allRoles = forGraph.getRoles(player).diff(Set(player))

    allEquivalentRoles.forall(r => allRoles.exists(isInstanceOf(r, _)))
  }

  private def checkProhibitions(player: Any, role: Any): Boolean = {
    val candidates = roleProhibitions.vertexSet().filter(isInstanceOf(_, role))
    if (candidates.isEmpty) return true

    val allProhibitedRoles = candidates.map(new DepthFirstIterator[String, DefaultEdge](roleProhibitions, _).toSet).flatten
    val allRoles = forGraph.getRoles(player).diff(Set(player))

    val rs = allProhibitedRoles.size == allRoles.size match {
      case false => allProhibitedRoles.filter(r => allRoles.exists(isInstanceOf(r, _)))
      case true => Set[String]()
    }

    allProhibitedRoles.diff(rs).diff(candidates).forall(r => !allRoles.exists(isInstanceOf(r, _)))
  }

  def validateRoleConstraints(player: Any, role: Any): Boolean =
    checkImplications(player, role) && checkEquivalence(player, role) && checkProhibitions(player, role)
}
