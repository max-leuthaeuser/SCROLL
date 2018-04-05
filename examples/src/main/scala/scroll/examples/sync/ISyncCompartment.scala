package scroll.examples.sync

import scroll.internal.Compartment
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.roles.ISyncRole

trait ISyncCompartment extends Compartment {
  
  protected var doSync = false;
  protected var syncer = ListBuffer[ISyncRole]()
  
  protected def getNextRole(classname: Object) : ISyncRole
  protected def getFirstRole(classname: Object) : ISyncRole

  def addSyncer(sync: ISyncRole): Unit = {
    syncer = syncer :+ sync
  }

  def getSyncer(): ListBuffer[ISyncRole] = {
    return syncer
  }
  
  def clearSyncer(): Unit = {
    syncer.clear()
  }
  
  def getFirstIntegrationRole(classname: Object) : ISyncRole = {
    var role: ISyncRole = this.getFirstRole(classname)
    this.addSyncer(role)
    return role
  }
  
  def getNextIntegrationRole(classname: Object) : ISyncRole = {
    var role: ISyncRole = this.getNextRole(classname)
    this.addSyncer(role)
    return role
  }  
  
  def isFirstIntegration(classname: Object): Boolean
  
  def isIntegration(classname: Object): Boolean
  
  def getNewInstance() : ISyncCompartment
  
  def getRuleName() : String
}