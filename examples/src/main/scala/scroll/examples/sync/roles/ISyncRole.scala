package scroll.examples.sync.roles

import scroll.examples.sync.ISyncCompartment

/**
  * Interface for the synchronization roles.
  */
trait ISyncRole {

  /**
    * Function to get the synchronization compartment from a role instance.
    */
  def getOuterCompartment(): ISyncCompartment
}