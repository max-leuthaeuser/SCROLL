package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IDestructor

trait IDestructionCompartment extends Compartment {
  
  def getDestructorForClassName(classname: Object) : IDestructor
}