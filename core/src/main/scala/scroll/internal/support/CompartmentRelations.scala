package scroll.internal.support

import scroll.internal.ICompartment

trait CompartmentRelations {
  self: ICompartment =>

  /**
    * Declaring a bidirectional is-part-of relation between compartment.
    */
  def union(other: ICompartment): ICompartment = {
    require(null != other)
    other.partOf(this)
    this.partOf(other)
    this
  }

  /**
    * Merge role graphs to this and set other role graph to this one.
    */
  def combine(other: ICompartment): ICompartment = {
    require(null != other)
    if (other.plays != this.plays) {
      plays.addPart(other.plays)
      other.plays = this.plays
    }
    this
  }

  /**
    * Declaring a is-part-of relation between compartments.
    */
  def partOf(other: ICompartment): Unit = {
    require(null != other)
    val _ = plays.addPart(other.plays)
  }

  /**
    * Removing is-part-of relation between compartments.
    */
  def notPartOf(other: ICompartment): Unit = {
    require(null != other)
    plays.detach(other.plays)
  }

}
