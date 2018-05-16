package scroll.examples.sync

import scroll.internal.Compartment
import scala.collection.immutable.Set
import scroll.examples.sync.roles.ISyncRole

/**
  * Interface for each synchronization rule.
  */
trait ISyncCompartment extends Compartment {

  /**
    * Variable to proof if he is actual in a sync process.
    */
  protected var doSync = false;
  /**
    * All sync roles of this synchronization rule.
    */
  protected var syncer = Set.empty[ISyncRole]

  /**
    * Get roles for integration classes. Should give less roles than getNextRole.
    */
  protected def getFirstRole(classname: Object): ISyncRole

  /**
    * Get roles for all integration classes.
    */
  protected def getNextRole(classname: Object): ISyncRole

  private def addSyncer(sync: ISyncRole): Unit = {
    syncer += sync
  }

  /**
    * Get the list of all sync roles.
    */
  def getSyncer(): Set[ISyncRole] = {
    return syncer
  }

  /**
    * Clear the list of all sync roles.
    */
  def clearSyncer(): Unit = {
    syncer = Set.empty[ISyncRole]
  }

  /**
    * Get roles for integration classes. Should give less roles than getNextRole.
    */
  def getFirstIntegrationRole(classname: Object): ISyncRole = {
    var role: ISyncRole = this.getFirstRole(classname)
    this.addSyncer(role)
    return role
  }

  /**
    * Get roles for all integration classes.
    */
  def getNextIntegrationRole(classname: Object): ISyncRole = {
    var role: ISyncRole = this.getNextRole(classname)
    this.addSyncer(role)
    return role
  }

  /**
    * Get boolean if first integration.
    */
  def isFirstIntegration(classname: Object): Boolean

  /**
    * Get boolean if next integration
    */
  def isNextIntegration(classname: Object): Boolean

  /**
    * Create a new instance of this class.
    */
  def getNewInstance(): ISyncCompartment

  /**
    * Get the name of this rule.
    */
  def getRuleName(): String
}