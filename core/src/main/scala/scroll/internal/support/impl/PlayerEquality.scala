package scroll.internal.support.impl

import scroll.internal.compartment.impl.AbstractCompartment
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.PlayerEqualityApi

import scala.reflect.ClassTag

class PlayerEquality(private val roleGraph: RoleGraphProxyApi) extends PlayerEqualityApi {

  override def equalsPlayer[W <: AnyRef: ClassTag](
    a: AbstractCompartment#IPlayer[W, ?],
    b: AbstractCompartment#IPlayer[W, ?]
  ): Boolean = {
    val coreA = roleGraph.plays.coreFor(a.wrapped)
    val coreB = roleGraph.plays.coreFor(b.wrapped)
    if (coreA.sizeIs == 1) {
      coreA.headOption.exists(coreB.lastOption.contains)
    } else if (coreB.sizeIs == 1) {
      coreB.headOption.exists(coreA.lastOption.contains)
    } else {
      coreA == coreB
    }
  }

  override def equalsAny[W <: AnyRef: ClassTag](a: AbstractCompartment#IPlayer[W, ?], b: AnyRef): Boolean = {
    val coreA = roleGraph.plays.coreFor(a.wrapped)
    if (coreA.sizeIs == 1) {
      coreA.headOption.contains(b)
    } else {
      coreA.lastOption.contains(b)
    }
  }

}
