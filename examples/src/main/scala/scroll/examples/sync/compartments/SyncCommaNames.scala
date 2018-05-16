package scroll.examples.sync.compartments

import scroll.internal.util.Log.info
import scroll.examples.sync.ISyncCompartment
import scroll.examples.sync.roles.ISyncRole
import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelC.SimplePerson
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.models.modelB.Member

/**
  * Synchronization compartment for full name split with comma.
  */
class SyncCommaNames extends ISyncCompartment {

  def getNextRole(classname: Object): ISyncRole = {
    if (classname.isInstanceOf[Family] || classname.isInstanceOf[Member] || classname.isInstanceOf[Person] || classname.isInstanceOf[SimplePerson])
      return new Sync()
    return null
  }

  def getFirstRole(classname: Object): ISyncRole = {
    if (classname.isInstanceOf[Member] || classname.isInstanceOf[Person] || classname.isInstanceOf[SimplePerson])
      return new Sync()
    return null
  }

  def isNextIntegration(classname: Object): Boolean = {
    if (classname.isInstanceOf[Family] || classname.isInstanceOf[Member] || classname.isInstanceOf[Person] || classname.isInstanceOf[SimplePerson])
      return true
    return false
  }

  def isFirstIntegration(classname: Object): Boolean = {
    if (classname.isInstanceOf[Member] || classname.isInstanceOf[Person] || classname.isInstanceOf[SimplePerson])
      return true
    return false
  }

  def getNewInstance(): ISyncCompartment = new SyncCommaNames()

  def getRuleName(): String = "SyncNameRule2"

  class Sync() extends ISyncRole {

    def getOuterCompartment(): ISyncCompartment = SyncCommaNames.this

    def listNames(): Unit = {
      println("Print Related Object List Size: " + syncer.size);
      syncer.foreach { a => info("E: " + a + " +++ D: " + (+a).isDeleted() + " **** First: " + (+a).getFirstName() + " Last: " + (+a).getLastName() + " Full: " + (+a).getFullName()) }
    }

    def changeFullName(): Unit = {
      if (!doSync) {
        doSync = true;
        println("Change FullName To: " + (+this getFullName()) + " Player: " + this.player);
        var fullName: String = +this getFullName();
        var result: Array[java.lang.String] = fullName.split(" ");
        var firstName: String = result.head;
        var lastName: String = result.last;
        syncer.foreach { a =>
          if (!a.equals(this)) {
            (+a).setLastName(lastName);
            (+a).setFullName(fullName);
            (+a).setCompleteName(lastName + "," + firstName);
            (+a).setFirstName(firstName);
          }
        }
        doSync = false;
      }
    }

    def changeCompleteName(): Unit = {
      if (!doSync) {
        doSync = true;
        println("Change CompleteName To: " + (+this getCompleteName()) + " Player: " + this.player);
        var completeName: String = +this getCompleteName();
        var result: Array[java.lang.String] = completeName.split(",");
        var lastName: String = result.head;
        var firstName: String = result.last;
        syncer.foreach { a =>
          if (!a.equals(this)) {
            (+a).setLastName(lastName);
            (+a).setFullName(firstName + " " + lastName);
            (+a).setCompleteName(completeName);
            (+a).setFirstName(firstName);
          }
        }
        doSync = false;
      }
    }

    def changeLastName(): Unit = {
      if (!doSync) {
        doSync = true;
        println("Change LastName To: " + (+this getLastName()) + " Player: " + this.player);
        var lastName: String = +this getLastName();
        syncer.foreach { a =>
          if (!a.equals(this)) {
            var fullName = (+a).getFullName();
            if (fullName.isRight) {
              var test: String = fullName
              var result: Array[java.lang.String] = test.split(" ");
              var firstName: String = result.head;
              (+a).setFullName(firstName + " " + lastName);
            }
            var completeName = (+a).getCompleteName();
            if (completeName.isRight) {
              var test: String = completeName
              var result: Array[java.lang.String] = test.split(",");
              var firstName: String = result.last;
              (+a).setCompleteName(lastName + "," + firstName);
            }
            (+a).setLastName(lastName);
          }
        }
        doSync = false;
      }
    }

    def changeFirstName(): Unit = {
      if (!doSync) {
        doSync = true;
        println("Change FirstName To: " + (+this getFirstName()) + " Player: " + this.player);
        var firstName: String = +this getFirstName();
        syncer.foreach { a =>
          if (!a.equals(this)) {
            var fullName = (+a).getFullName();
            if (fullName.isRight) {
              var test: String = fullName
              var result: Array[java.lang.String] = test.split(" ");
              var lastName: String = result.last;
              (+a).setFullName(firstName + " " + lastName);
            }
            var completeName = (+a).getCompleteName();
            if (completeName.isRight) {
              var test: String = completeName
              var result: Array[java.lang.String] = test.split(",");
              var lastName: String = result.head;
              (+a).setCompleteName(lastName + "," + firstName);
            }
            (+a).setFirstName(firstName);
          }
        }
        doSync = false;
      }
    }
  }

}