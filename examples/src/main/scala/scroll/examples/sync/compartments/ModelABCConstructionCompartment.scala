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
import scroll.examples.sync.models.modelC.SimplePerson
import scroll.examples.sync.models.modelA.Male
import scroll.examples.sync.models.modelA.Female

object ModelABCConstructionCompartment extends IConstructionCompartment  {
  def getConstructorForClassName(classname: Object) : IConstructor = {
    if (classname.isInstanceOf[Family])
      return new FamilyConstruct()
    else if (classname.isInstanceOf[Member])
      return new MemberConstruct()
    else if (classname.isInstanceOf[Person])
      return new PersonConstruct()
    else if (classname.isInstanceOf[SimplePerson])
      return new RegisterConstruct()
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
      var register: SimplePerson = new SimplePerson(firstName + " " + lastName, male)
      if (male)
        person = new Male(firstName + " " + lastName)
      else
        person = new Female(firstName + " " + lastName)

      //println("Step 3");//Step 3: Add RoleManager roles and Delete roles
      var rmMA = SynchronizationCompartment.createRoleManager();
      var rmMC = SynchronizationCompartment.createRoleManager();
      person play rmMA
      register play rmMC

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(register)
      var personDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(person)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      man play memberDelete
      rmMA play personDelete
      rmMC play registerDelete

      //println("Step 4");//Step 4: Add the related Role Manager
      man.addRelatedManager(rmMA)
      man.addRelatedManager(rmMC)
      man.addRelatedManager(rmFamily)
      rmMA.addRelatedManager(man)
      rmMA.addRelatedManager(rmMC)
      rmMA.addRelatedManager(rmFamily)
      rmMC.addRelatedManager(rmMA)
      rmMC.addRelatedManager(man)
      rmMC.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(man)
      rmFamily.addRelatedManager(rmMC)
      rmFamily.addRelatedManager(rmMA)

      //println("Step 5");//Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register //union synchonisiert die rollen graphen
      SynchronizationCompartment combine person //union synchonisiert die rollen graphen

      //println("Step 6");//Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMA play this.getNextIntegrationRole(person)
        rmMC play this.getNextIntegrationRole(register)
        if (family != null) {
          if (rmFamily != null)
          {            
            rmFamily play this.getNextIntegrationRole(family)
          }
        }
        SynchronizationCompartment combine this
      }

      //println("Step 7");//Step 7: Fill Test Lists
      ModelElementLists.addElement(comp)
      ModelElementLists.addElement(person)
      ModelElementLists.addElement(register)
      
      println("Finish Member Construct");
      SynchronizationCompartment.underConstruction = false;
    }
  }

  class RegisterConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      SynchronizationCompartment.underConstruction = true;

      println("Start Register Construct");

      //Step 1: Get construction values
      var fullName: String = +this getCompleteName ();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;
      var male: Boolean = +this getMale ();

      //Step 2: Create the object in the other models
      var family = new Family(lastName);
      var member: Member = null;
      var person: Person = null;      
      if (male) {
        person = new Male(firstName + " " + lastName)
        member = new Member(firstName, family, false, false, true, false);
      } else {
        person = new Female(firstName + " " + lastName)
        member = new Member(firstName, family, false, false, false, true);
      }
      
      //Step 3: Add RoleManager roles and Delete roles      
      var rmFamily = SynchronizationCompartment.createRoleManager();
      var rmMA = SynchronizationCompartment.createRoleManager();
      var rmMB = SynchronizationCompartment.createRoleManager();      
      family play rmFamily
      person play rmMA
      member play rmMB

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      var personDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(person)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(member)
      var familyDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(family)      
      rmFamily play familyDelete
      man play registerDelete
      rmMA play personDelete
      rmMB play memberDelete

      //Step 4: Add the related Role Manager
      man.addRelatedManager(rmMA)
      man.addRelatedManager(rmMB)
      man.addRelatedManager(rmFamily)
      rmMA.addRelatedManager(man)
      rmMA.addRelatedManager(rmMB)
      rmMA.addRelatedManager(rmFamily)
      rmMB.addRelatedManager(rmMA)
      rmMB.addRelatedManager(man)
      rmMB.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(man)
      rmFamily.addRelatedManager(rmMB)
      rmFamily.addRelatedManager(rmMA)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine family
      SynchronizationCompartment combine person
      SynchronizationCompartment combine member

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMA play this.getNextIntegrationRole(person)
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
      ModelElementLists.addElement(person)
      ModelElementLists.addElement(comp)
      
      println("Finish Register Construct");

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
      var register: SimplePerson = null;
      if (comp.isInstanceOf[Male]) {
        register = new SimplePerson(firstName + " " + lastName, true);
        member = new Member(firstName, family, false, false, true, false);
      } else {
        register = new SimplePerson(firstName + " " + lastName, false)
        member = new Member(firstName, family, false, false, false, true);
      }

      //Step 3: Add RoleManager roles and Delete roles        
      var rmMB = SynchronizationCompartment.createRoleManager();
      var rmMC = SynchronizationCompartment.createRoleManager();
      var rmFamily = SynchronizationCompartment.createRoleManager();
      family play rmFamily
      register play rmMC
      member play rmMB

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(register)
      var personDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(member)
      var familyDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(family)      
      rmFamily play familyDelete
      man play personDelete
      rmMC play registerDelete
      rmMB play memberDelete

      //Step 4: Add the related Role Manager
      man.addRelatedManager(rmMB)
      man.addRelatedManager(rmMC)
      man.addRelatedManager(rmFamily)
      rmMB.addRelatedManager(man)
      rmMB.addRelatedManager(rmMC)
      rmMB.addRelatedManager(rmFamily)
      rmMC.addRelatedManager(rmMB)
      rmMC.addRelatedManager(man)
      rmMC.addRelatedManager(rmFamily)      
      rmFamily.addRelatedManager(man)
      rmFamily.addRelatedManager(rmMB)
      rmFamily.addRelatedManager(rmMC)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register
      SynchronizationCompartment combine family
      SynchronizationCompartment combine member //union synchonisiert die rollen graphen

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMB play this.getNextIntegrationRole(member)
        rmMC play this.getNextIntegrationRole(register)
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
      ModelElementLists.addElement(register)
      
      println("Finish Person Construct");

      SynchronizationCompartment.underConstruction = false;
    }
  }
}