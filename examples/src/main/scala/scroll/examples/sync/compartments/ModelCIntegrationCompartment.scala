package scroll.examples.sync.compartments

import scroll.examples.sync.PlayerSync
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.IIntegrationCompartment
import scroll.examples.sync.ISyncCompartment
import scroll.examples.sync.roles.IIntegrator
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.SynchronizationCompartment
import scroll.examples.sync.models.modelA.Male
import scroll.examples.sync.roles.IRoleManager
import scroll.examples.sync.roles.ISyncRole
import scroll.examples.sync.ModelElementLists
import scroll.examples.sync.models.modelC.SimplePerson

object ModelCIntegrationCompartment extends IIntegrationCompartment {
  
  def getIntegratorForClassName(classname: Object) : IIntegrator = {
    if (classname.isInstanceOf[Person])
      return new PersonConstruct()
    return null
  }
  
  class PersonConstruct() extends IIntegrator {

    def integrate(comp: PlayerSync): Unit = {
      
      SynchronizationCompartment.underConstruction = true;
      
      println("Start Register Integration "  + comp);
      //Step 1: Get construction values
      var fullName: String = +this getFullName ();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;

      //Step 2: Create the object in the other models
      var register: SimplePerson = null;
      if (comp.isInstanceOf[Male])
        register = new SimplePerson(firstName + " " + lastName, true);
      else
        register = new SimplePerson(firstName + " " + lastName, false)

      //Step 3: Add RoleManager roles and Delete roles        
      var rmMC = SynchronizationCompartment.createRoleManager();
      register play rmMC

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(register)
      rmMC play registerDelete

      //Step 4: Add the related Role Manager
      var manager = (+comp).getManager()
      if (manager.isRight)
      {
        var realManager: IRoleManager = manager.right.get
        var related = realManager.getRelatedManager()
        realManager.addRelatedManager(rmMC)
        rmMC.addRelatedManager(realManager)
        related.foreach { r =>
          r.addRelatedManager(rmMC)
          rmMC.addRelatedManager(r)
        }        
      }

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register
      
      //Step 6: Integrate in Synchronization Rules
      var player = ModelCIntegrationCompartment.this.plays.getRoles(comp)      
      player.foreach { r =>
        if (r.isInstanceOf[ISyncRole]) {
          var syncRole: ISyncRole = r.asInstanceOf[ISyncRole]
          var syncComp: ISyncCompartment = syncRole.getOuterCompartment()
          var newRole = syncComp.getNextIntegrationRole(register)
          if (newRole != null) {
            rmMC play newRole
            SynchronizationCompartment combine syncComp
          }
        }
      }
      
      //Step 7: Fill Test Lists
      ModelElementLists.addElement(register)
      
      println("Finish Register Integration");
      
      SynchronizationCompartment.underConstruction = false;
    }
  }
}