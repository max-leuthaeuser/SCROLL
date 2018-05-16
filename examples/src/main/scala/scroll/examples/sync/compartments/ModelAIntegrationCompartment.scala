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
import scroll.examples.sync.models.modelA.Female

object ModelAIntegrationCompartment extends IIntegrationCompartment {

  def getIntegratorForClassName(classname: Object): IIntegrator = {
    if (classname.isInstanceOf[SimplePerson])
      return new SimplePersonConstruct()
    return null
  }

  class SimplePersonConstruct() extends IIntegrator {

    def integrate(comp: PlayerSync): Unit = {
      println("Start Person Integration " + comp);

      //Step 1: Get construction values
      var fullName: String = +this getCompleteName();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;
      var male: Boolean = +this getMale();

      //Step 2: Create the object in the other models
      var person: Person = null;
      if (male) {
        person = new Male(firstName + " " + lastName)
      } else {
        person = new Female(firstName + " " + lastName)
      }

      //Step 3: Add RoleManager roles and Delete roles        
      var rmMA = SynchronizationCompartment.createRoleManager();
      person play rmMA

      var personDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(person)
      rmMA play personDelete

      //Step 4: Add the related Role Manager
      var manager = (+comp).getManager()
      if (manager.isRight) {
        var realManager: IRoleManager = manager.right.get
        var related = realManager.getRelatedManager()
        realManager.addRelatedManager(rmMA)
        rmMA.addRelatedManager(realManager)
        related.foreach { r =>
          r.addRelatedManager(rmMA)
          rmMA.addRelatedManager(r)
        }
      }

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine person

      //Step 6: Integrate in Synchronization Rules
      var player = ModelAIntegrationCompartment.this.plays.roles(comp)
      player.foreach { r =>
        if (r.isInstanceOf[ISyncRole]) {
          var syncRole: ISyncRole = r.asInstanceOf[ISyncRole]
          var syncComp: ISyncCompartment = syncRole.getOuterCompartment()
          var newRole = syncComp.getNextIntegrationRole(person)
          if (newRole != null) {
            rmMA play newRole
            SynchronizationCompartment combine syncComp
          }
        }
      }

      //Step 7: Fill Test Lists
      ModelElementLists.addElement(person)

      println("Finish Register Integration");
    }
  }

}