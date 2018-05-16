package scroll.examples.sync

import scroll.internal.MultiCompartment

class PlayerSync extends MultiCompartment {

  var deleted: Boolean = false;

  def isDeleted(): Boolean = {
    return deleted;
  }

  def buildClass(): Unit = {
    println("Create New Class");
    if (!SynchronizationCompartment.isUnderConstruction()) {
      this play SynchronizationCompartment.createRoleManager()
      SynchronizationCompartment combine this
      +this manage (this)
    }
  }

  def deleteObjectFromSynchro(): Unit = {
    println("Delete Object");
    +this deleteRoleFunction()
    deleted = true;
  }
}