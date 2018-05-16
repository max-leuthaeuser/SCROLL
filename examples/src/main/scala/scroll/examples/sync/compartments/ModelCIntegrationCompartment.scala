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

  def getIntegratorForClassName(classname: Object): IIntegrator = {
    if (classname.isInstanceOf[Person])
      return new PersonConstruct()
    return null
  }

  class PersonConstruct() extends IIntegrator {

    def integrate(comp: PlayerSync): Unit = {
      println("Start Register Integration " + comp);

      //Step 1: Get construction values
      var fullName: String = +this getFullName();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;

      //Step 2: Create the object in the other models
      var register: SimplePerson = null;
      if (comp.isInstanceOf[Male])
        register = new SimplePerson(firstName + " " + lastName, true)
      else
        register = new SimplePerson(firstName + " " + lastName, false)
      
      var manager = +comp getManager()
      
      //Step 3: Create Containers
      createContainerElement(register, SynchronizationCompartment.createRoleManager(), comp, manager)

      //Step 4: Finish Creation
      ModelCIntegrationCompartment.this.makeCompleteIntegrationProcess(containers)
      
      println("Finish Register Integration");
    }
  }

}