package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync

/**
 * Interface for the integration roles.
 */
trait IIntegrator {
  
  /**
   * General integration function for external call.
   */
  def integrate(comp: PlayerSync) : Unit
}