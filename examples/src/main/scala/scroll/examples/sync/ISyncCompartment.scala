package scroll.examples.sync

import scroll.internal.Compartment
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.roles.ISyncRole

trait ISyncCompartment extends Compartment {
  var syncer = ListBuffer[ISyncRole]()

  def addSyncer(sync: ISyncRole): Unit = {
    syncer = syncer :+ sync
  }

  def getSyncer(): ListBuffer[ISyncRole] = {
    return syncer
  }

  var doSync = false;
  
  def getSyncRole(classname: Object) : ISyncRole = {
    var role: ISyncRole = this.getRole(classname)
    this.addSyncer(role)
    return role
  }
  
  protected def getRole(classname: Object) : ISyncRole
  
  def getNewInstance() : ISyncCompartment
  
  def getRuleName() : String
  
  def isFirstIntegration(classname: Object): Boolean
}