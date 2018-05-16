package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IIntegrator

/**
  * Interface for each integration rule.
  */
trait IIntegrationCompartment extends Compartment {

  /**
    * Return a role instance that handles the integration process for a new model to this instance.
    */
  def getIntegratorForClassName(classname: Object): IIntegrator
}