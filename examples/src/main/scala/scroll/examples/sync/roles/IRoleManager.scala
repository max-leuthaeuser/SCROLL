package scroll.examples.sync.roles

import scala.collection.mutable.ListBuffer
import scroll.examples.sync.PlayerSync

/**
 * Interface for the manager roles.
 */
trait IRoleManager {
  private var relatedManager = ListBuffer[IRoleManager]()

  /**
   * Add a related manager to the list.
   */
  def addRelatedManager(related: IRoleManager): Unit = {
    if (related == null || related.equals(this))
      return
    relatedManager = relatedManager :+ related
  }

  /**
   * Get the list of related managers.
   */
  def getRelatedManager(): ListBuffer[IRoleManager] = {
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
    //println("## Remove This Manger " + relatedManager.size);
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
    relatedManager.clear()
    //println("## Clear Related Manger " + relatedManager.size);
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