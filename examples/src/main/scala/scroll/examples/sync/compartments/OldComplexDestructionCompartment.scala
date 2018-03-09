package scroll.examples.sync.compartments

import scroll.internal.Compartment
import scroll.examples.sync.PlayerSync
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.IDestructionCompartment
import scroll.examples.sync.roles.IDestructor
import scroll.examples.sync.models.modelC.SimplePerson
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.roles.IRoleManager

/**
 * Does the same as the GeneralDestructor
 * Calls the destruction method from all related RoleManagers and then deletes all roles from this player.
 */
object OldComplexDestructionCompartment extends IDestructionCompartment {
  
  def getDestructorForClassName(classname: Object) : IDestructor = {
    if (classname.isInstanceOf[Family])
      return null
    else if (classname.isInstanceOf[Member])
      return new MemberDelete()
    else if (classname.isInstanceOf[Person])
      return new PersonDelete()
    else if (classname.isInstanceOf[SimplePerson])
      return new RegisterDelete()
    return null
  }
  
  class PersonDelete() extends IDestructor {

    /*var syncer = List[PlayerSync]()

    def addDeletionObject(comp: PlayerSync) {
      syncer = syncer :+ comp
    }*/

    def deleteRoleFunction(): Unit = {
      println("##Delete element Player: " + this.player);
      var relatedManager = (+this).getRelatedManager()
      (+this).clearListsOfRelatedManager()
      //call delete method in all related role managers
      if (relatedManager.isRight)
      {
        //println("In IF STATEMENT" + relatedManager.right.get);
        var list: ListBuffer[IRoleManager] = relatedManager.right.get
        list.foreach { m =>
          println("Manager: " + m);
          (+m).deleteObjectFromSynchro()
        }
      }
      //clear now the related manager list
      (+this).clearRelatedManager()
      //delete all roles this element has      
      var player = this.player;
      if (player.isRight)
      {
        //println("In IF STATEMENT" + player.right.get);
        var test: PlayerSync = player.right.get.asInstanceOf[PlayerSync]
        var roles = plays.getRoles(test)
        println("--Roles Player: " + roles)
        roles.foreach { r =>
          plays.removePlayer(test)
        }
        println("--Roles Player: " + plays.getRoles(test))        
      }
      /*var manager = (+this).getManager()
      if (manager.isRight)
      {
        var test: IRoleManager = manager.right.get
        println("--Roles Player: " + plays.getRoles(test))
      }
      println("Roles Comp: " + plays.getRoles(comp))
      //syncer.foreach { a => (+a) deleted = true }
      //syncer = List[PlayerSync]()*/
    }
  }

  class RegisterDelete() extends IDestructor {

    def deleteRoleFunction(): Unit = {
      println("##Delete element Player: " + this.player);
      var relatedManager = (+this).getRelatedManager()
      (+this).clearListsOfRelatedManager()
      //call delete method in all related role managers
      if (relatedManager.isRight)
      {
        //println("In IF STATEMENT" + relatedManager.right.get);
        var list: ListBuffer[IRoleManager] = relatedManager.right.get
        list.foreach { m =>
          println("Manager: " + m);
          (+m).deleteObjectFromSynchro()
        }
      }
      //clear now the related manager list
      (+this).clearRelatedManager()
      //delete all roles this element has      
      var player = this.player;
      if (player.isRight)
      {
        //println("In IF STATEMENT" + player.right.get);
        var test: PlayerSync = player.right.get.asInstanceOf[PlayerSync]
        var roles = plays.getRoles(test)
        println("--Roles Player: " + roles)
        roles.foreach { r =>
          plays.removePlayer(test)
        }
        println("--Roles Player: " + plays.getRoles(test))        
      }
    }
  }

  class MemberDelete() extends IDestructor {

    def deleteRoleFunction(): Unit = {
      println("##Delete element Player: " + this.player);
      var relatedManager = (+this).getRelatedManager()
      (+this).clearListsOfRelatedManager()
      //call delete method in all related role managers
      if (relatedManager.isRight)
      {
        //println("In IF STATEMENT" + relatedManager.right.get);
        var list: ListBuffer[IRoleManager] = relatedManager.right.get
        list.foreach { m =>
          println("Manager: " + m);
          (+m).deleteObjectFromSynchro()
        }
      }
      //clear now the related manager list
      (+this).clearRelatedManager()
      //delete all roles this element has      
      var player = this.player;
      if (player.isRight)
      {
        //println("In IF STATEMENT" + player.right.get);
        var test: PlayerSync = player.right.get.asInstanceOf[PlayerSync]
        var roles = plays.getRoles(test)
        println("--Roles Player: " + roles)
        roles.foreach { r =>
          plays.removePlayer(test)
        }
        println("--Roles Player: " + plays.getRoles(test))        
      }
    }
  }
  
}