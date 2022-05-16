package scroll.internal.support.impl

import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.RolePlayingApi
import scroll.internal.support.RoleRestrictionsApi

import scala.reflect.ClassTag

class RolePlaying(
  private[this] val roleGraph: RoleGraphProxyApi,
  private[this] val roleRestrictions: RoleRestrictionsApi
) extends RolePlayingApi {

  override def transferRole[F <: AnyRef: ClassTag, T <: AnyRef: ClassTag, R <: AnyRef: ClassTag](
    coreFrom: F,
    coreTo: T,
    role: R
  ): Unit = {
    require(null != coreFrom)
    require(null != coreTo)
    require(null != role)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  override def addPlaysRelation[C <: AnyRef: ClassTag, R <: AnyRef: ClassTag](core: C, role: R): Unit = {
    require(null != core)
    require(null != role)
    roleRestrictions.validate(core, role)
    roleGraph.plays.addBinding(core, role)
  }

  override def removePlaysRelation(core: AnyRef, role: AnyRef): Unit = {
    require(null != core)
    require(null != role)
    roleGraph.plays.removeBinding(core, role)
  }

  override def removePlayer(player: AnyRef): Unit = roleGraph.plays.removePlayer(player)

  override def allPlayers: Seq[AnyRef] = roleGraph.plays.allPlayers
}
