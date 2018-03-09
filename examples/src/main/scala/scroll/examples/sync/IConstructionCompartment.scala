package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IConstructor

trait IConstructionCompartment extends Compartment {
  
  def getConstructorForClassName(classname: Object) : IConstructor
}