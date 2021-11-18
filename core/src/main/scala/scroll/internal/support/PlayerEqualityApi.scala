package scroll.internal.support

import scroll.internal.compartment.impl.AbstractCompartment

import scala.reflect.ClassTag

trait PlayerEqualityApi {

  def equalsPlayer[W <: AnyRef: ClassTag](
    a: AbstractCompartment#IPlayer[W, _],
    b: AbstractCompartment#IPlayer[W, _]
  ): Boolean

  def equalsAny[W <: AnyRef: ClassTag](a: AbstractCompartment#IPlayer[W, _], b: AnyRef): Boolean

}
