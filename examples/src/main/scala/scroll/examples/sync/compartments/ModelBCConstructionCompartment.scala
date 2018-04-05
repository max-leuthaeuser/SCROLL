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

object ModelBCConstructionCompartment extends IConstructionCompartment  {
  def getConstructorForClassName(classname: Object) : IConstructor = {
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
      SynchronizationCompartment.underConstruction = true;
      println("Start Family Construct");
      
      //Step 3: Create Containers 
      var cc = new ConstructionContainer()
      cc.fillContainer(true, comp, man)
      containers = containers :+ cc

      //Step 4: Finish Creation
      ModelBCConstructionCompartment.this.makeCompleteConstructionProcess(containers)     
      
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
      var register: SimplePerson = new SimplePerson(firstName + " " + lastName, male)
      
      //Step 3: Create Containers 
      var cc = new ConstructionContainer()
      cc.fillContainer(true, comp, man)
      containers = containers :+ cc
      
      if (family != null) {
        cc = new ConstructionContainer()
        cc.fillContainer(false, family, rmFamily)
        containers = containers :+ cc
      }
      
      cc = new ConstructionContainer()
      cc.fillContainer(true, register, SynchronizationCompartment.createRoleManager())
      containers = containers :+ cc
      cc.playerInstance play cc.managerInstance
            
      //Step 4: Finish Creation
      ModelBCConstructionCompartment.this.makeCompleteConstructionProcess(containers)
      
      /*//println("Step 3");//Step 3: Add RoleManager roles and Delete roles
      var rmMC = SynchronizationCompartment.createRoleManager();
      register play rmMC

      var registerDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(register)
      var memberDelete = SynchronizationCompartment.destructionCompartment.getDestructorForClassName(comp)
      man play memberDelete
      rmMC play registerDelete

      //println("Step 4");//Step 4: Add the related Role Manager
      man.addRelatedManager(rmMC)
      man.addRelatedManager(rmFamily)
      rmMC.addRelatedManager(man)
      rmMC.addRelatedManager(rmFamily)
      rmFamily.addRelatedManager(man)
      rmFamily.addRelatedManager(rmMC)

      //println("Step 5");//Step 5: Synchronize the Compartments
      SynchronizationCompartment combine register //union synchonisiert die rollen graphen

      //println("Step 6");//Step 6: Create the Synchronization mechanisms for the name
      SynchronizationCompartment.syncCompartmentInfoList.foreach { s =>
        //if it should be for first integration than add
        if (s.isFirstIntegration(comp)) {
          var sync : ISyncCompartment = s.getNewInstance()
          man play sync.getNextIntegrationRole(comp)
          if (sync.isIntegration(register))
            rmMC play sync.getNextIntegrationRole(register)
          if (family != null && rmFamily != null && sync.isIntegration(family))
            rmFamily play sync.getNextIntegrationRole(family)
          SynchronizationCompartment combine sync
        }
      }
      
      /*new SyncSpaceNames() {
        man play this.getNextIntegrationRole(comp)
        rmMC play this.getNextIntegrationRole(register)
        if (family != null) {
          if (rmFamily != null)
          {            
            rmFamily play this.getNextIntegrationRole(family)
          }
        }
        SynchronizationCompartment combine this
      }*/

      //println("Step 7");//Step 7: Fill Test Lists
      ModelElementLists.addElement(comp)
      ModelElementLists.addElement(register)*/
      
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
      if (male) {
        member = new Member(firstName, family, false, false, true, false);
      } else {
        member = new Member(firstName, family, false, false, false, true);
      }
      
      //Step 3: Add RoleManager roles and Delete roles 
      var cc = new ConstructionContainer()
      cc.fillContainer(true, comp, man)
      containers = containers :+ cc
      
      cc = new ConstructionContainer()
      cc.fillContainer(true, family, SynchronizationCompartment.createRoleManager())
      containers = containers :+ cc
      cc.playerInstance play cc.managerInstance
      
      cc = new ConstructionContainer()
      cc.fillContainer(true, member, SynchronizationCompartment.createRoleManager())
      containers = containers :+ cc
      cc.playerInstance play cc.managerInstance
      
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
      SynchronizationCompartment.underConstruction = false;
    }
  }
}