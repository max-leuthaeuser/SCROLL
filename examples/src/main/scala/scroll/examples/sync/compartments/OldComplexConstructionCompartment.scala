package scroll.examples.sync.compartments

import scroll.internal.Compartment
import scroll.examples.sync.IConstructionCompartment
import scroll.examples.sync.SynchronizationCompartment
import scroll.examples.sync.roles.IConstructor
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelC.SimplePerson
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.roles.IRoleManager
import scroll.examples.sync.PlayerSync
import scroll.examples.sync.ModelElementLists
import scroll.examples.sync.models.modelA.Male
import scroll.examples.sync.models.modelA.Female

/**
  * Old construction Process for Model A, B, and C.
  */
object OldComplexConstructionCompartment extends IConstructionCompartment {

  def getConstructorForClassName(classname: Object): IConstructor = {
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
      println("Start Family Construct");

      new SyncKnownList() {
        man play this.getNextIntegrationRole(man)
        SynchronizationCompartment combine this
      }

      ModelElementLists.addElement(comp)

      println("Finish Person/Register for Member");
    }
  }

  class MemberConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Start Person/Register for Member");

      //println("Step 1");//Step 1: Get construction values
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

      var registerDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(register)
      var personDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(person)
      var memberDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(comp)
      man play memberDelete
      rmMA play personDelete
      rmMC play registerDelete

      //println("Step 4");//Step 4: Add the deleted Objects
      /*registerDelete.addDeletionObject(comp)
      registerDelete.addDeletionObject(person)
      personDelete.addDeletionObject(register)
      personDelete.addDeletionObject(comp)
      memberDelete.addDeletionObject(person)
      memberDelete.addDeletionObject(register)*/

      //println("Step 4.5");//Step 4.5: Add the related Role Manager
      man.addRelatedManager(rmMA)
      man.addRelatedManager(rmMC)
      rmMA.addRelatedManager(man)
      rmMA.addRelatedManager(rmMC)
      rmMC.addRelatedManager(rmMA)
      rmMC.addRelatedManager(man)

      //println("Step 5");//Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register //union synchonisiert die rollen graphen //TODO: .this nötig
      SynchronizationCompartment combine person //union synchonisiert die rollen graphen

      //println("Step 6");//Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(man)
        rmMA play this.getNextIntegrationRole(rmMA)
        rmMC play this.getNextIntegrationRole(rmMC)
        if (family != null) {
          if (rmFamily != null) {
            rmFamily play this.getNextIntegrationRole(rmFamily)
          }
          //addSyncer(family)
        }
        //addSyncer(comp)
        //addSyncer(register)
        //addSyncer(person)
        SynchronizationCompartment combine this
      }

      //println("Step 7");//Step 7: Synchronize the Compartments
      /*if (father != null) {
        ComplexSynchronization.this combine father
      }
      if (son != null) {
        ComplexSynchronization.this combine son
      }
      if (mother != null) {
        ComplexSynchronization.this combine mother
      }
      if (daughter != null) {
        ComplexSynchronization.this combine daughter
      }
      ComplexSynchronization.this combine person
      ComplexSynchronization.this combine comp*/

      //println("Fill Test Lists");      
      if (family != null) {
        ModelElementLists.addElement(family)
      }
      ModelElementLists.addElement(comp)
      ModelElementLists.addElement(person)
      ModelElementLists.addElement(register)

      println("Finish Person/Register for Member");
    }
  }

  class RegisterConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Create Person/Member for RegisterEntry");

      //Step 1: Get construction values
      var fullName: String = +this getCompleteName();
      var result: Array[java.lang.String] = fullName.split(" ");
      var firstName: String = result.head;
      var lastName: String = result.last;
      var male: Boolean = +this getMale();

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
      //family.setRoleMangerFromPlayerSync(rmFamily)
      person play rmMA
      member play rmMB

      var registerDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(comp)
      var personDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(person)
      var memberDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(member)
      man play registerDelete
      rmMA play personDelete
      rmMB play memberDelete

      //Step 4: Add the deleted Objects
      /*registerDelete.addDeletionObject(member)
      registerDelete.addDeletionObject(person)
      personDelete.addDeletionObject(member)
      personDelete.addDeletionObject(comp)
      memberDelete.addDeletionObject(person)
      memberDelete.addDeletionObject(comp)*/

      //Step 4.5: Add the related Role Manager
      man.addRelatedManager(rmMA)
      man.addRelatedManager(rmMB)
      rmMA.addRelatedManager(man)
      rmMA.addRelatedManager(rmMB)
      rmMB.addRelatedManager(rmMA)
      rmMB.addRelatedManager(man)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine family //TODO: .this notwenig
      SynchronizationCompartment combine person
      SynchronizationCompartment combine member

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(man)
        rmMA play this.getNextIntegrationRole(rmMA)
        rmMB play this.getNextIntegrationRole(rmMB)
        rmFamily play this.getNextIntegrationRole(rmFamily)

        //addSyncer(comp)
        //addSyncer(person)
        //addSyncer(member)
        //addSyncer(family)
        SynchronizationCompartment combine this
      }

      new SyncKnownList() {
        rmFamily play this.getNextIntegrationRole(rmFamily)
        SynchronizationCompartment combine this
      }
      //rmFamily play new SyncFamily

      //Step 7: Synchronize the Compartments
      /*ComplexSynchronization.this combine family
      ComplexSynchronization.this combine member
      ComplexSynchronization.this combine person
      ComplexSynchronization.this combine comp*/

      ModelElementLists.addElement(family)
      ModelElementLists.addElement(member)
      ModelElementLists.addElement(person)
      ModelElementLists.addElement(comp)
    }
  }

  class PersonConstruct() extends IConstructor {

    def construct(comp: PlayerSync, man: IRoleManager): Unit = {
      println("Create Member/Register for Person");

      //Step 1: Get construction values
      var fullName: String = +this getFullName();
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
      //family.setRoleMangerFromPlayerSync(rmFamily)
      register play rmMC
      member play rmMB

      var registerDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(register)
      var personDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(comp)
      var memberDelete = SynchronizationCompartment.getDestructionRule().getDestructorForClassName(member)
      man play personDelete
      rmMC play registerDelete
      rmMB play memberDelete

      //Step 4: Add the deleted Objects
      /*registerDelete.addDeletionObject(member)
      registerDelete.addDeletionObject(comp)
      personDelete.addDeletionObject(member)
      personDelete.addDeletionObject(register)
      memberDelete.addDeletionObject(register)
      memberDelete.addDeletionObject(comp)*/

      //Step 4.5: Add the related Role Manager
      man.addRelatedManager(rmMB)
      man.addRelatedManager(rmMC)
      rmMB.addRelatedManager(man)
      rmMB.addRelatedManager(rmMC)
      rmMC.addRelatedManager(rmMB)
      rmMC.addRelatedManager(man)

      //Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register
      SynchronizationCompartment combine family
      SynchronizationCompartment combine member //union synchonisiert die rollen graphen

      //Step 6: Create the Synchronization mechanisms for the name
      new SyncSpaceNames() {
        man play this.getNextIntegrationRole(man)
        rmMB play this.getNextIntegrationRole(rmMB)
        rmMC play this.getNextIntegrationRole(rmMC)
        rmFamily play this.getNextIntegrationRole(rmFamily)

        //addSyncer(comp)
        //addSyncer(register)
        //addSyncer(member)
        //addSyncer(family)
        SynchronizationCompartment combine this
      }

      new SyncKnownList() {
        rmFamily play this.getNextIntegrationRole(rmFamily)
        SynchronizationCompartment combine this
      }
      //rmFamily play new SyncFamily

      //Step 7: Synchronize the Compartments
      /*ComplexSynchronization.this combine register
      ComplexSynchronization.this combine family
      ComplexSynchronization.this combine member
      ComplexSynchronization.this combine comp*/

      println("Fill Test Lists");

      ModelElementLists.addElement(family)
      ModelElementLists.addElement(member)
      ModelElementLists.addElement(comp)
      ModelElementLists.addElement(register)
    }
  }

}