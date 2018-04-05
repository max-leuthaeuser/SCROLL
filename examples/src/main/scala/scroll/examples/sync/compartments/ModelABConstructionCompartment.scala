package scroll.examples.sync.compartments

import scroll.examples.sync.PlayerSync
import scroll.examples.sync.IConstructionCompartment
import scroll.examples.sync.SynchronizationCompartment
import scroll.examples.sync.roles.IRoleManager
import scroll.examples.sync.roles.IConstructor
import scroll.examples.sync.ModelElementLists
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelA.Male
import scroll.examples.sync.models.modelA.Female

object ModelABConstructionCompartment extends IConstructionCompartment  {
  def getConstructorForClassName(classname: Object) : IConstructor = {
    if (classname.isInstanceOf[Family])
      return new FamilyConstruct()
    else if (classname.isInstanceOf[Member])
      return new MemberConstruct()
    else if (classname.isInstanceOf[Person])
      return new PersonConstruct()
    return null
  }
  
  class FamilyConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      SynchronizationCompartment.underConstruction = true;

      println("Start Family Construct");
      
      //Step 3: Add RoleManager roles and Delete roles
      var familyDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)      
      man play familyDelete

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncKnownList() {
        man play this.getNextIntegrationRole(comp)
        SynchronizationCompartment combine this
      }

      //Step 7: Fill Test Lists      
      ModelElementLists.addElement(comp)
      
      println("Finish Family Construct");
      SynchronizationCompartment.underConstruction = false;
    }
  }
  
  class MemberConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      SynchronizationCompartment.underConstruction = true;

      println("Start Member Construct");

      //println("Step 1");//Step 1: Get construction values
      var firstName: String = +this firstName;
      var lastName: String = +this getLastName ();
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

      //println("Step 2");//Step 2: Create the object in the other models
      var person: Person = null
      if (male)
        person = new Male(firstName + " " + lastName)
      else
        person = new Female(firstName + " " + lastName)

      //println("Step 3");//Step 3: Add RoleManager roles and Delete roles
      var rmMA = SynchronizationCompartment.createRoleManager();
      person play rmMA

      var personDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(person)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)      
      man play memberDelete
      rmMA play personDelete

      //println("Step 4");//Step 4: Add the related Role Manager
      man.addRelatedManager(rmMA)
      man.addRelatedManager(rmFamily)
      rmMA.addRelatedManager(man)
      rmMA.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(rmMA)
      rmFamily.addRelatedManager(man)

      //println("Step 5");//Step 5: Synchronize the Compartments
      SynchronizationCompartment combine person

      //println("Step 6");//Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(man)
        rmMA play this.getNextIntegrationRole(rmMA)
        if (family != null) {
          if (rmFamily != null)
          {            
            rmFamily play this.getNextIntegrationRole(rmFamily)
          }
        }
        SynchronizationCompartment combine this
      }

      //println("Step 7");//Step 7: Fill Test Lists
      ModelElementLists.addElement(comp)
      ModelElementLists.addElement(person)
      
      println("Finish Member Construct");
      SynchronizationCompartment.underConstruction = false;
    }
  }

  class PersonConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      SynchronizationCompartment.underConstruction = true;

      println("Start Person Construct");
      //Step 1: Get construction values
      var fullName: String = +this getFullName ();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;

      //Step 2: Create the object in the other models
      var family = new Family(lastName);
      var member: Member = null;
      if (comp.isInstanceOf[Male]) {
        member = new Member(firstName, family, false, false, true, false);
      } else {
        member = new Member(firstName, family, false, false, false, true);
      }

      //Step 3: Add RoleManager roles and Delete roles        
      var rmMB = SynchronizationCompartment.createRoleManager();
      var rmFamily = SynchronizationCompartment.createRoleManager();
      family play rmFamily
      member play rmMB

      var personDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(member)
      var familyDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(family)
      man play personDelete
      rmMB play memberDelete
      rmFamily play familyDelete

      //Step 4: Add the related Role Manager
      man.addRelatedManager(rmMB)
      man.addRelatedManager(rmFamily)
      rmMB.addRelatedManager(man)
      rmMB.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(rmMB)
      rmFamily.addRelatedManager(man)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine family
      SynchronizationCompartment combine member //union synchonisiert die rollen graphen

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMB play this.getNextIntegrationRole(member)
        rmFamily play this.getNextIntegrationRole(family)
        SynchronizationCompartment combine this
      }

      new SyncKnownList() {
        rmFamily play this.getNextIntegrationRole(family)
        SynchronizationCompartment combine this
      }
      
      //Step 7: Fill Test Lists
      ModelElementLists.addElement(family)
      ModelElementLists.addElement(member)
      ModelElementLists.addElement(comp)
      
      println("Finish Person Construct");

      SynchronizationCompartment.underConstruction = false;
    }
  }
}