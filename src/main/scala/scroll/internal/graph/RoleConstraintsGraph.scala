package scroll.internal.graph

import org.jgrapht.graph.{DefaultEdge, DefaultDirectedGraph}
import org.jgrapht.traverse.DepthFirstIterator
import scroll.internal.ReflectiveHelper

import scala.collection.JavaConversions._
import scala.reflect.Manifest

class RoleConstraintsGraph(private val forGraph: RoleGraph[Any]) {
  private val roleImplications = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])
  private val roleEquivalents = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])
  private val roleProhibitions = new DefaultDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

  private def isInstanceOf(mani: String, that: Any) =
    ReflectiveHelper.classSimpleClassName(that.getClass.toString) == ReflectiveHelper.classSimpleClassName(mani)

  def addImplication[A: Manifest, B: Manifest]() {
    roleImplications.addVertex(manifest[A].toString())
    roleImplications.addVertex(manifest[B].toString())
    roleImplications.addEdge(manifest[A].toString(), manifest[B].toString())
  }

  def addEquivalents[A: Manifest, B: Manifest]() {
    roleEquivalents.addVertex(manifest[A].toString())
    roleEquivalents.addVertex(manifest[B].toString())
    roleEquivalents.addEdge(manifest[A].toString(), manifest[B].toString())
    roleEquivalents.addEdge(manifest[B].toString(), manifest[A].toString())
  }

  def addProhibition[A: Manifest, B: Manifest]() {
    roleProhibitions.addVertex(manifest[A].toString())
    roleProhibitions.addVertex(manifest[B].toString())
    roleProhibitions.addEdge(manifest[A].toString(), manifest[B].toString())
  }

  private def checkImplications(player: Any, role: Any): Boolean = {
    val candidates = roleImplications.vertexSet().filter(isInstanceOf(_, role))
    if (candidates.isEmpty) return true

    val allImplicitRoles = candidates.map(new DepthFirstIterator[String, DefaultEdge](roleImplications, _).toSet).flatten
    val allRoles = forGraph.getRoles(player).diff(Set(player))

    allImplicitRoles.forall(r => allRoles.exists(isInstanceOf(r, _)))
  }

  private def checkEquivalents(player: Any, role: Any): Boolean = {
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
    checkImplications(player, role) && checkEquivalents(player, role) && checkProhibitions(player, role)
}
