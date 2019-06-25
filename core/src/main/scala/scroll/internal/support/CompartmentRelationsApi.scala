package scroll.internal.support

import scroll.internal.compartment.impl.AbstractCompartment

trait CompartmentRelationsApi {
  /**
    * Declaring a bidirectional is-part-of relation between compartment.
    */
  def union(other: AbstractCompartment): Unit

  /**
    * Merge role graphs to this and set other role graph to this one.
    */
  def combine(other: AbstractCompartment): Unit

  /**
    * Declaring a is-part-of relation between compartments.
    */
  def partOf(other: AbstractCompartment): Unit

  /**
    * Removing is-part-of relation between compartments.
    */
  def notPartOf(other: AbstractCompartment): Unit

}
