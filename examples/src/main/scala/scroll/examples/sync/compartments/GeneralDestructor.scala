package scroll.examples.sync.compartments

import scala.collection.immutable.Set
import scroll.examples.sync.PlayerSync
import scroll.examples.sync.IDestructionCompartment
import scroll.examples.sync.roles.IDestructor
import scroll.examples.sync.roles.IRoleManager

/**
  * Calls the destruction method from all related RoleManagers and then deletes all roles from this player.
  */
object GeneralDestructor extends IDestructionCompartment {

  def getDestructorForClassName(classname: Object): IDestructor = new DeleteRole

  class DeleteRole() extends IDestructor {

    def deleteRoleFunction(): Unit = {
      println("##Delete roles and related from Player: " + this.player);
      var relatedManager = (+this).getRelatedManager()
      (+this).clearListsOfRelatedManager()
      //call delete method in all related role managers
      if (relatedManager.isRight) {
        //println("In IF STATEMENT" + relatedManager.right.get);
        var list: Set[IRoleManager] = relatedManager.right.get
        list.foreach { m =>
          //println("Manager: " + m);
          (+m).deleteObjectFromSynchro()
        }
      }
      //clear now the related manager list
      (+this).clearRelatedManager()
      //delete all roles this element has      
      var player = this.player;
      if (player.isRight) {
        //println("In IF STATEMENT" + player.right.get);
        var test: PlayerSync = player.right.get.asInstanceOf[PlayerSync]
        var roles = plays.roles(test)
        roles.foreach { r =>
          plays.removePlayer(r)
        }
        //println("--Roles Player: " + plays.getRoles(test))        
      }
    }
  }

}