package scroll.examples.sync

import scroll.internal.Compartment
import scroll.examples.sync.roles.IIntegrator
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.roles.ISyncRole

/**
  * Interface for each integration rule.
  */
trait IIntegrationCompartment extends Compartment {

  /**
   * Return a role instance that handles the integration process for a new model to this instance.
   */
  def getIntegratorForClassName(classname: Object): IIntegrator

  /**
   * Add Manager roles to all constructed elements.
   */
  private def addManagerRoles(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      cc.getNewPlayerInstance() play cc.getNewManagerInstance()
    }
  }

  /**
   * Add the delete roles for the elements in the IntegrationContainer.
   */
  private def addDeleteRoles(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      cc.getNewManagerInstance() play SynchronizationCompartment.getDestructionRule().getDestructorForClassName(cc.getNewPlayerInstance())
    }
  }

  /**
   * Add the related RoleManagers for the elements in the IntegrationContainer.
   */
  private def addRelatedRoleManager(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      var related = cc.getOldManagerInstance().getRelatedManager()
      related.foreach { r =>
        r.addRelatedManager(cc.getNewManagerInstance())
        cc.getNewManagerInstance().addRelatedManager(r)
      }
      cc.getNewManagerInstance().addRelatedManager(cc.getOldManagerInstance())
      cc.getOldManagerInstance().addRelatedManager(cc.getNewManagerInstance())
    }
  }

  /**
   * Combine the SynchronizationCompartment with all Players from the IntegrationContainer.
   */
  private def synchronizeCompartments(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      SynchronizationCompartment combine cc.getNewPlayerInstance()
    }
  }

  /**
   * Create the Synchronization mechanisms for the elements in the IntegrationContainer.
   */
  private def bindSynchronizationRules(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      var player = plays.roles(cc.getOldPlayerInstance())
      player.foreach { r =>
        if (r.isInstanceOf[ISyncRole]) {
          var syncRole: ISyncRole = r.asInstanceOf[ISyncRole]
          var syncComp: ISyncCompartment = syncRole.getOuterCompartment()
          var newRole = syncComp.getNextIntegrationRole(cc.getNewPlayerInstance())
          if (newRole != null) {
            cc.getNewManagerInstance() play newRole
          }
        }
      }
    }
  }

  /**
   * Fill the test lists with all Players from the IntegrationContainer.
   */
  private def fillTestLists(containers: ListBuffer[IntegrationContainer]): Unit = {
    containers.foreach { cc =>
      ModelElementLists.addElement(cc.getNewPlayerInstance())
    }
  }

  /**
   * Do the integration process automatically.
   */
  protected def makeCompleteIntegrationProcess(containers: ListBuffer[IntegrationContainer]): Unit = {
    this.addManagerRoles(containers)
    this.addDeleteRoles(containers)
    this.addRelatedRoleManager(containers)
    this.synchronizeCompartments(containers)
    this.bindSynchronizationRules(containers)
    this.fillTestLists(containers)
  }
}