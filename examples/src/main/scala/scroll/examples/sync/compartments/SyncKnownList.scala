package scroll.examples.sync.compartments

import scroll.internal.Compartment
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.ISyncCompartment
import scroll.examples.sync.roles.ISyncRole
import scroll.examples.sync.PlayerSync
import scroll.examples.sync.SynchronizationCompartment

class SyncKnownList extends ISyncCompartment {
  
  def getRole(classname: Object) : ISyncRole = new SyncFamily
  
  def getNewInstance() : ISyncCompartment = new SyncKnownList
  
  def getRuleName() : String = "SyncKnownListRule"
  
  def isFirstIntegration(classname: Object): Boolean = true
  
  class SyncFamily() extends ISyncRole {

    var familySync = false;
    
    def getOuterCompartment(): ISyncCompartment = SyncKnownList.this

    def changeFamily(): Unit = {
      if (!familySync) {
        familySync = true;

        println("Change Family");

        var source = this.player;
        var daughters: List[PlayerSync] = +this getDaughters ();
        var father: PlayerSync = +this getFather ();
        var mother: PlayerSync = +this getMother ();
        var sons: List[PlayerSync] = +this getSons ();
        
        var allMembers = List[PlayerSync]()
        if (father != null)
          allMembers = allMembers :+ father;
        if (mother != null)
          allMembers = allMembers :+ mother;
        if (sons.size > 0)
          allMembers = allMembers ++ sons;
        if (daughters.size > 0)
          allMembers = allMembers ++ daughters;

        println("AllMember Size: " + allMembers.size + " M: " + allMembers);

        //get Register entries of all
        var changingRegister = List[PlayerSync]()
        allMembers.foreach {
          m =>
            var regardedManager: ListBuffer[SynchronizationCompartment.RoleManager] = (+m).getRelatedManager()
            //println("RegardedManager: " + regardedManager);
            regardedManager.foreach {
              r =>
                r.player.foreach {
                  s => 
                    //println("Player Elements: " + s);
                    var completeName = (+s).getCompleteName();
                    if (completeName.isRight) {
                      changingRegister = changingRegister :+ s.asInstanceOf[PlayerSync]
                    }
                }
            }
        }

        changingRegister.foreach {
          r =>
            changingRegister.foreach {
              i =>
                (+r).addKnown(i)
            }
            println("Register: " + r);
        }

        println("SynchroEnd");
        familySync = false
      }
    }

    def changeFather(): Unit = {
      changeFamily()
    }

    def changeMother(): Unit = {
      changeFamily()
    }

    def changeSon(): Unit = {
      changeFamily()
    }

    def changeDaughter(): Unit = {
      changeFamily()
    }
  }
  
  /*class SyncFamily() {

    var familySync = false;

    def changeFamily(): Unit = {
      if (!familySync) {
        familySync = true;

        println("Change Family");

        var source = this.player;
        var daughters: List[Member] = +this getDaughters ();
        var father: Member = +this getFather ();
        var mother: Member = +this getMother ();
        var sons: List[Member] = +this getSons ();
        
        var allMembers = List[Member]()
        if (father != null)
          allMembers = allMembers :+ father;
        if (mother != null)
          allMembers = allMembers :+ mother;
        if (sons.size > 0)
          allMembers = allMembers ++ sons;
        if (daughters.size > 0)
          allMembers = allMembers ++ daughters;

        println("AllMember Size: " + allMembers.size + " M: " + allMembers);

        //get Register entries of all
        var changingRegister = List[PersonForRegister]()
        allMembers.foreach {
          m =>
            var regardedManager: List[ComplexSynchronization.RoleManager] = +m getRelatedManager ()
            //println("RegardedManager: " + regardedManager);
            regardedManager.foreach {
              r =>
                r.player.foreach {
                  s => //println("Player Elements: " + s);
                    if (s.isInstanceOf[PersonForRegister]) {
                      changingRegister = changingRegister :+ s.asInstanceOf[PersonForRegister]
                    }
                }
            }
        }

        changingRegister.foreach {
          r =>
            changingRegister.foreach {
              i =>
                r.addKnown(i)
            }
            println("Register: " + r);
        }

        println("SynchroEnd");
        familySync = false
      }
    }

    def changeFather(): Unit = {
      changeFamily()
    }

    def changeMother(): Unit = {
      changeFamily()
    }

    def changeSon(): Unit = {
      changeFamily()
    }

    def changeDaughter(): Unit = {
      changeFamily()
    }
  }*/
}