package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.IntegrationContainer

/**
  * Interface for the integration roles.
  */
trait IIntegrator {

  /**
   * Container list for the integration process.
   */
  protected var containers = ListBuffer[IntegrationContainer]()

  /**
   * Create a container element with the incoming configuration.
   */
  protected def createContainerElement(newPlayer: PlayerSync, newManager: IRoleManager, oldPlayer: PlayerSync, oldManager: IRoleManager): Unit = {
    if (oldPlayer == null || oldManager == null)
      return
    var cc = new IntegrationContainer(newPlayer, newManager, oldPlayer, oldManager)
    containers += cc
  }
  
  /**
   * General integration function for external call.
   */
  def integrate(comp: PlayerSync) : Unit
}