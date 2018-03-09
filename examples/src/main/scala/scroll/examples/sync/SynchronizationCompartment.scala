package scroll.examples.sync

import util.control.Breaks._
import scala.collection.Seq
import scroll.examples.sync.roles.IRoleManager
import scroll.examples.sync.compartments.ModelABConstructionCompartment
import scroll.examples.sync.roles.IDestructor
import scroll.examples.sync.roles.ISyncRole
import scroll.examples.sync.compartments.GeneralDestructor

object SynchronizationCompartment extends ISynchronizationCompartment {

  def createRoleManager(): IRoleManager = new RoleManager()

  var constructionCompartment: IConstructionCompartment = null// ModelABConstructionCompartment
  var destructionCompartment: IDestructionCompartment = null// GeneralDestructor
  
  private def debugCompleteRoleGraphOutput(): Unit = {
    println("")
    println("Plays: " + plays)
    var nodes = plays.allPlayers;
    nodes.foreach { n =>
      println("Output N: " + n + " Player: " + n.player)
    }
    println("")
  }
  
  private def debugSyncRoleGraphOutput(): Unit = {
    println("")
    println("Plays: " + plays)
    var nodes = plays.allPlayers;
    nodes.foreach { n =>
      if (n.isInstanceOf[ISyncRole]) {
        var role: ISyncRole = n.asInstanceOf[ISyncRole]
        var compart: ISyncCompartment = role.getOuterCompartment()
        println("Output N: " + n + " Player: " + n.player + " Comp: " + compart + " RN: " + compart.getRuleName())
      }
    }
    println("")
  }
  
  private def debugPlayerRolesOutput(): Unit = {
    println("")
    println("Plays: " + plays)
    var nodes = plays.allPlayers;
    nodes.foreach { n =>
      if (n.isInstanceOf[PlayerSync]) {
        var player: PlayerSync = n.asInstanceOf[PlayerSync]
        println("Output N: " + plays.getRoles(player))
      }
    }
    println("")
  }

  def changeConstructionRule(construct: IConstructionCompartment): Unit = {
    if (construct == null)
      return ;
    constructionCompartment = construct
  }

  def changeDestructionRule(destruct: IDestructionCompartment): Unit = {
    if (destruct == null)
      return ;
    if (destructionCompartment == null) {
      destructionCompartment = destruct
      return
    }
    //debugCompleteRoleGraphOutput()
    var nodes = plays.allPlayers; //get all nodes
    //delete all destruction roles
    nodes.foreach { n =>
      if (n.isInstanceOf[IDestructor])
        plays.removePlayer(n)
    }
    //debugCompleteRoleGraphOutput()
    //add all new ones
    nodes = plays.allPlayers;
    nodes.foreach { n =>
      if (n.isInstanceOf[IRoleManager]) {
        //add new role here
        var player = n.player
        if (player.isRight) {
          var realPlayer = player.right.get
          var newRole = destruct.getDestructorForClassName(realPlayer)
          n play newRole
        }
      }
    }
    debugCompleteRoleGraphOutput()
    destructionCompartment = destruct
    this combine destructionCompartment
  }

  def integrateNewModel(integrationRule: IIntegrationCompartment): Unit = {
    this combine integrationRule
    var nodes = plays.allPlayers;
    nodes.foreach { n =>
      if (n.isInstanceOf[PlayerSync]) {
        var player: PlayerSync = n.asInstanceOf[PlayerSync]
        var role = integrationRule.getIntegratorForClassName(player)
        //println("RM: " + n + " Role: " + role)
        if (role != null) {
          player play role
          this combine integrationRule
          (+player).integrate(player)
          plays.removePlayer(role)
        }
      }
    }
  }

  def addSynchronizationRule(newRule: ISyncCompartment): Unit = {
    var running = true;
    var nodes = Seq[AnyRef]()
    //debugSyncRoleGraphOutput()
    while (running) {
      breakable {
        running = false;
        nodes = plays.allPlayers; //get all nodes
        nodes.foreach { n =>
          if (n.isInstanceOf[RoleManager]) {
            //proof if the role manager does not play this rule
            var proof = true;
            var realManager: RoleManager = n.asInstanceOf[RoleManager]
            var player = realManager.player
            if (player.isRight) {
              var realPlayer = player.right.get
              var relatedRoles = plays.getRoles(n)
              relatedRoles.foreach { r =>
                if (r.isInstanceOf[ISyncRole]) {
                  var syncRole: ISyncRole = r.asInstanceOf[ISyncRole]
                  var syncComp: ISyncCompartment = syncRole.getOuterCompartment()
                  if (syncComp.getRuleName().equals(newRule.getRuleName()) || !newRule.isFirstIntegration(realPlayer))
                    proof = false
                }
              }
              //if role manager was not integrated before then integrate now
              if (proof) {
                //add new role to the player
                //the new compartment
                var newComp: ISyncCompartment = newRule.getNewInstance()
                var newRole = newComp.getSyncRole(realPlayer)
                if (newRole != null)
                  realManager play newRole
                else
                  proof = false;

                if (proof) {
                  //add roles to related role manager because on is added to this one
                  var related = realManager.getRelatedManager()                  
                  related.foreach { r =>
                    var player = r.player
                    if (player.isRight) {
                      var realPlayer = player.right.get
                      var newRole = newComp.getSyncRole(realPlayer)
                      r play newRole
                    }
                  }
                  this combine newComp
                  running = true;
                  break
                }
              }
            }
          }
        }
      }
    }
    //debugPlayerRolesOutput()
    //debugSyncRoleGraphOutput()
  }  

  def deleteRule(ruleName: String): Unit = {
    var nodes = plays.allPlayers; //get all nodes
    nodes.foreach { n =>
      if (n.isInstanceOf[ISyncRole]) {
        var role: ISyncRole = n.asInstanceOf[ISyncRole]
        var compart: ISyncCompartment = role.getOuterCompartment()
        //println("Destruct1: " + n.isInstanceOf[ISyncRole] + " N: " + n + " Player: " + n.player + " Comp: " + compart + " RN: " + compart.getRuleName() + " From: " + from)
        if (compart.getRuleName().equals(ruleName)) {
          compart.syncer.clear()
          plays.removePlayer(n)
        }
      }
    }
    //debugCompleteRoleGraphOutput()
  }

  def changeRuleFromTo(from: String, to: ISyncCompartment): Unit = {
    var running = true;
    //debugSyncRoleGraphOutput()
    var nodes = Seq[AnyRef]()    
    while (running) {
      breakable {
        running = false;
        nodes = plays.allPlayers; //get all nodes
        nodes.foreach { n =>
          if (n.isInstanceOf[ISyncRole]) {
            var role: ISyncRole = n.asInstanceOf[ISyncRole]
            var compart: ISyncCompartment = role.getOuterCompartment()
            //println("Destruct1: " + n.isInstanceOf[ISyncRole] + " N: " + n + " Player: " + n.player + " Comp: " + compart + " RN: " + compart.getRuleName() + " From: " + from)
            if (compart.getRuleName().equals(from)) {
              //exchange this with a new compartment              
              var newComp: ISyncCompartment = to.getNewInstance()
              compart.syncer.foreach { r =>
                var manager = (+r).getManager()
                if (manager.isRight) {
                  var realManager: RoleManager = manager.right.get
                  var player = r.player
                  if (player.isRight) {
                    var realPlayer = player.right.get
                    var newRole = newComp.getSyncRole(realPlayer)
                    plays.removePlayer(r)
                    realManager play newRole
                  }
                }
              }
              //rograph combination
              //ComplexSynchronization.this combine newComp
              this combine newComp
              //delete compartment
              compart.syncer.clear()
              running = true;
              break
            }
          }
        }
      }
    }
    //debugSyncRoleGraphOutput()
  }

  def oldDestructionCompartmentChange(destruct: IDestructionCompartment): Unit = {
    var nodes = plays.allPlayers; //get all nodes
    nodes.foreach { n =>
      println("Destruct: " + n.isInstanceOf[IDestructor] + " N: " + n + " Player: " + n.player)
      if (n.isInstanceOf[IDestructor]) {
        var manager = (+n).getManager()
        if (manager.isRight) {
          var realManager: RoleManager = manager.right.get
          var player = n.player
          if (player.isRight) {
            var realPlayer = player.right.get
            var newRole = destruct.getDestructorForClassName(realPlayer)
            plays.removePlayer(n)
            realManager play newRole
          }
        }
      }
    }
    //debugCompleteRoleGraphOutput()
    destructionCompartment = destruct
  }

  class RoleManager() extends IRoleManager {

    def manage(value: PlayerSync): Unit = {
      //println("Player: " + this.player.toSeq + " || " + (+this).getClass + " || " + value.isInstanceOf[Person]);
      println("****Create Related Roles from the object " + this.player + " " + (+this));
      SynchronizationCompartment.this combine constructionCompartment
      SynchronizationCompartment.this combine destructionCompartment

      var construct = constructionCompartment.getConstructorForClassName(value)
      if (construct != null) {
        this play construct
        +this construct (value, this)
        plays.removePlayer(construct)
        //println("'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''" + construct + " Value: " + value)
      }

      println("~~~~~~~~~~~~~~~~~~~~~~All Players: " + plays.allPlayers)

      /*if (value.isInstanceOf[Person]) {
        var constructor = new ConstructionCompartment.PersonConstruct
        this play constructor
        //ComplexSynchronization.this combine value
        +this construct (value, this)
        //this drop constructor
        plays.removePlayer(constructor)
        //println("~~~~~~~~~~~~~~~~~~~~~~All Players: " + plays.allPlayers)
      } else if (value.isInstanceOf[Member]) {
        var constructor = new ConstructionCompartment.MemberConstruct
        this play constructor
        //ComplexSynchronization.this combine value
        +this construct (value, this)
        plays.removePlayer(constructor)   
      } else if (value.isInstanceOf[PersonForRegister]) {
        var constructor = new ConstructionCompartment.RegisterConstruct
        this play constructor
        //ComplexSynchronization.this combine value
        +this construct (value, this)
        plays.removePlayer(constructor)
      } else if (value.isInstanceOf[Family]) {
        var constructor = new ConstructionCompartment.FamilyConstruct
        this play constructor
        //ComplexSynchronization.this combine value
        +this construct (value, this)
        plays.removePlayer(constructor)       
      }*/
    }
  }
}