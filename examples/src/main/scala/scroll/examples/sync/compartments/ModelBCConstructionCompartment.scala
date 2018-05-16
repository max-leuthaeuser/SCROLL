package scroll.examples.sync.compartments

import scroll.examples.sync.PlayerSync
import scroll.examples.sync.IConstructionCompartment
import scroll.examples.sync.SynchronizationCompartment
import scroll.examples.sync.roles.IRoleManager
import scroll.examples.sync.roles.IConstructor
import scroll.examples.sync.ModelElementLists
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelC.SimplePerson
import scroll.examples.sync.ISyncCompartment
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.ConstructionContainer

/**
  * Construction Process for Model B, and C.
  */
object ModelBCConstructionCompartment extends IConstructionCompartment {

  def getConstructorForClassName(classname: Object): IConstructor = {
    if (classname.isInstanceOf[Family])
      return new FamilyConstruct()
    else if (classname.isInstanceOf[Member])
      return new MemberConstruct()
    else if (classname.isInstanceOf[SimplePerson])
      return new RegisterConstruct()
    return null
  }

  class FamilyConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Start Family Construct");

      //Step 3: Create Containers 
      createContainerElement(true, true, comp, man)

      //Step 4: Finish Creation
      ModelBCConstructionCompartment.this.makeCompleteConstructionProcess(containers)

      println("Finish Family Construct");
    }
  }

  class MemberConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Start Member Construct");

      //Step 1: Get construction values
      var firstName: String = +this firstName;
      var lastName: String = +this getLastName();
      var family: Family = null
      var male: Boolean = true

      var father: Family = (+this).getFamilyFather()
      var son: Family = (+this).getFamilySon()
      var mother: Family = (+this).getFamilyMother()
      var daughter: Family = (+this).getFamilyDaughter()
      if (father != null)
        family = father
      else if (son != null)
        family = son
      else if (mother != null) {
        family = mother
        male = false
      } else if (daughter != null) {
        family = daughter
        male = false
      }

      var rmFamily: IRoleManager = null
      if (family != null) {
        var manager = (+family).getManager()
        if (manager.isRight)
          rmFamily = manager.right.get //manager.right.get.asInstanceOf[IRoleManager]
      }

      //Step 2: Create the object in the other models
      var register: SimplePerson = new SimplePerson(firstName + " " + lastName, male)

      //Step 3: Create Containers 
      createContainerElement(true, true, comp, man)
      createContainerElement(false, false, family, rmFamily)
      createContainerElement(false, true, register, SynchronizationCompartment.createRoleManager())

      //Step 4: Finish Creation
      ModelBCConstructionCompartment.this.makeCompleteConstructionProcess(containers)

      println("Finish Member Construct");
    }
  }

  class RegisterConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Start Register Construct");

      //Step 1: Get construction values
      var fullName: String = +this getCompleteName();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;
      var male: Boolean = +this getMale();

      //Step 2: Create the object in the other models
      var family = new Family(lastName);
      var member: Member = null;
      if (male) {
        member = new Member(firstName, family, false, false, true, false);
      } else {
        member = new Member(firstName, family, false, false, false, true);
      }

      //Step 3: Add RoleManager roles and Delete roles 
      createContainerElement(true, true, comp, man)
      createContainerElement(false, true, family, SynchronizationCompartment.createRoleManager())
      createContainerElement(false, true, member, SynchronizationCompartment.createRoleManager())

      //Step 4: Finish Creation
      ModelBCConstructionCompartment.this.makeCompleteConstructionProcess(containers)

      /*//Step 3: Add RoleManager roles and Delete roles      
      var rmFamily = SynchronizationCompartment.createRoleManager();
      var rmMB = SynchronizationCompartment.createRoleManager();      
      family play rmFamily
      member play rmMB

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(member)
      var familyDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(family)      
      rmFamily play familyDelete
      man play registerDelete
      rmMB play memberDelete

      //Step 4: Add the related Role Manager
      man.addRelatedManager(rmMB)
      man.addRelatedManager(rmFamily)
      rmMB.addRelatedManager(man)
      rmMB.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(man)
      rmFamily.addRelatedManager(rmMB)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine family
      SynchronizationCompartment combine member

      //Step 6: Create the Synchronization mechanisms for the name
      SynchronizationCompartment.syncCompartmentInfoList.foreach { s =>
        var sync : ISyncCompartment = null
        //if it should be for first integration than add
        if (s.isIntegration(comp)) {
          if (sync == null) {
            sync = s.getNewInstance()
          }
          man play sync.getNextIntegrationRole(comp)
        }
        if (s.isIntegration(family)) {
          if (sync == null) {
            sync = s.getNewInstance()
          }
          rmFamily play sync.getNextIntegrationRole(family)
        }
        if (s.isIntegration(member)) {
          if (sync == null) {
            sync = s.getNewInstance()
          }
          rmMB play sync.getNextIntegrationRole(member)
        }
        if (sync != null)
          SynchronizationCompartment combine sync
      }
      
      /*new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMB play this.getNextIntegrationRole(member)
        rmFamily play this.getNextIntegrationRole(family)
        SynchronizationCompartment combine this
      }

      new SyncKnownList() {
        rmFamily play this.getNextIntegrationRole(family)
        SynchronizationCompartment combine this
      }*/
      
      //Step 7: Fill Test Lists
      ModelElementLists.addElement(family)
      ModelElementLists.addElement(member)
      ModelElementLists.addElement(comp)*/

      println("Finish Register Construct");
    }
  }

}