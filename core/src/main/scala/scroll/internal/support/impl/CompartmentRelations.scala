package scroll.internal.support.impl

import scroll.internal.compartment.impl.AbstractCompartment
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.CompartmentRelationsApi

class CompartmentRelations(private[this] val roleGraph: RoleGraphProxyApi) extends CompartmentRelationsApi {

  override def union(other: AbstractCompartment): Unit = {
    require(null != other)
    other.roleGraph.plays.addPart(roleGraph.plays)
    val _ = roleGraph.plays.addPart(other.roleGraph.plays)
  }

  override def combine(other: AbstractCompartment): Unit = {
    require(null != other)
    if (other.roleGraph.plays != roleGraph.plays) {
      roleGraph.plays.addPart(other.roleGraph.plays)
      other.roleGraph.plays = roleGraph.plays
    }
  }

  override def partOf(other: AbstractCompartment): Unit = {
    require(null != other)
    val _ = roleGraph.plays.addPart(other.roleGraph.plays)
  }

  override def notPartOf(other: AbstractCompartment): Unit = {
    require(null != other)
    roleGraph.plays.detach(other.roleGraph.plays)
  }

}