package scroll.internal.support

import scroll.internal.compartment.impl.AbstractCompartment

import scala.reflect.ClassTag

trait PlayerEqualityApi {

  def equalsPlayer[W <: AnyRef: ClassTag](
    a: AbstractCompartment#IPlayer[W, ?],
    b: AbstractCompartment#IPlayer[W, ?]
  ): Boolean

  def equalsAny[W <: AnyRef: ClassTag](a: AbstractCompartment#IPlayer[W, ?], b: AnyRef): Boolean

}
