package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IRoleManager

trait ISynchronizationCompartment extends Compartment {
  
  var underConstruction: Boolean = false
  
  def createRoleManager(): IRoleManager  
}