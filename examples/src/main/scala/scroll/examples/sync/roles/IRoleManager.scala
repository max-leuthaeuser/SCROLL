package scroll.examples.sync.roles

import scala.collection.mutable.ListBuffer
import scroll.examples.sync.PlayerSync

trait IRoleManager {
  var relatedManager = ListBuffer[IRoleManager]()

  def addRelatedManager(related: IRoleManager): Unit = {
    relatedManager = relatedManager :+ related
  }

  def getRelatedManager(): ListBuffer[IRoleManager] = {
    return relatedManager
  }
    
  def removeRelatedManager(related: IRoleManager): Unit = {
    relatedManager -= related 
  }
  
  def removeThisManager(): Unit = {
    relatedManager.foreach { m =>
      m.removeRelatedManager(this)
    }
    //println("## Remove This Manger " + relatedManager.size);
  }
  
  def clearListsOfRelatedManager(): Unit = {
    relatedManager.foreach { m =>
      m.clearRelatedManager()
    }
  }
    
  def clearRelatedManager(): Unit = {
    relatedManager.clear()
    //println("## Clear Related Manger " + relatedManager.size);
  }

  def getManager(): IRoleManager = {
    return this
  }

  def manage(value: PlayerSync): Unit
}