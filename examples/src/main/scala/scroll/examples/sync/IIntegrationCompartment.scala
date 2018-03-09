package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IIntegrator

trait IIntegrationCompartment extends Compartment {
  
  def getIntegratorForClassName(classname: Object) : IIntegrator
}