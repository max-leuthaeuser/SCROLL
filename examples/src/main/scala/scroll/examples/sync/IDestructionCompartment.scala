package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IDestructor

/**
  * Interface for each destruction rule.
  */
trait IDestructionCompartment extends Compartment {

  /**
    * Return a role instance that handles the destruction process for the object.
    */
  def getDestructorForClassName(classname: Object): IDestructor
}