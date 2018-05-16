package scroll.examples.sync.roles

import scala.collection.immutable.Set
import scroll.examples.sync.PlayerSync

/**
 * Interface for the manager roles.
 */
trait IRoleManager {
  private var relatedManager = Set.empty[IRoleManager] //ListBuffer[IRoleManager]()

  /**
   * Add a related manager to the list.
   */
  def addRelatedManager(related: IRoleManager): Unit = {
    if (related == null || related.equals(this))
      return
    relatedManager += related
  }

  /**
   * Get the list of related managers.
   */
  def getRelatedManager(): Set[IRoleManager] = {
    return relatedManager
  }
    
  /**
   * Remove a related manager from the list.
   */
  def removeRelatedManager(related: IRoleManager): Unit = {
    if (related != null)
      relatedManager -= related 
  }
  
  /**
   * Remove this manager from the lists of all related managers.
   */
  def removeThisManager(): Unit = {
    relatedManager.foreach { m =>
      m.removeRelatedManager(this)
    }
  }
  
  /**
   * Clear the lists of all related managers,
   */
  def clearListsOfRelatedManager(): Unit = {
    relatedManager.foreach { m =>
      m.clearRelatedManager()
    }
  }
    
  /**
   * Clear the list of this role manager.
   */
  def clearRelatedManager(): Unit = {
    relatedManager = Set.empty[IRoleManager]
  }

  /**
   * Get this manager.
   */
  def getManager(): IRoleManager = {
    return this
  }

  /**
   * General manage function for external call.
   */
  def manage(value: PlayerSync): Unit
}