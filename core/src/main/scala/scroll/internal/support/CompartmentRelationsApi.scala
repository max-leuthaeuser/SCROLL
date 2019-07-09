package scroll.internal.support

import scroll.internal.compartment.CompartmentApi

trait CompartmentRelationsApi {
  /**
    * Declaring a bidirectional is-part-of relation between compartment.
    */
  def union(other: CompartmentApi): Unit

  /**
    * Merge role graphs to this and set other role graph to this one.
    */
  def combine(other: CompartmentApi): Unit

  /**
    * Declaring a is-part-of relation between compartments.
    */
  def partOf(other: CompartmentApi): Unit

  /**
    * Removing is-part-of relation between compartments.
    */
  def notPartOf(other: CompartmentApi): Unit

}
