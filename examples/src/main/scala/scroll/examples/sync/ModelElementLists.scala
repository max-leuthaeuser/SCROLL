package scroll.examples.sync

import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.models.modelC.SimplePerson

/**
 * Helper object to hold instances of all model elements.
 */
object ModelElementLists {
  
  private var families = List[Family]()
  private var members = List[Member]()
  private var persons = List[Person]()
  private var registers = List[SimplePerson]() 
  
  /**
   * Add a new element to the lists.
   */
  def addElement(obj: Object) {
    if (obj == null)
      return
    if (obj.isInstanceOf[Family])
      this.addFamily(obj.asInstanceOf[Family])
    if (obj.isInstanceOf[Member])
      this.addMember(obj.asInstanceOf[Member])
    if (obj.isInstanceOf[Person])
      this.addPersons(obj.asInstanceOf[Person])
    if (obj.isInstanceOf[SimplePerson])
      this.addRegister(obj.asInstanceOf[SimplePerson])
  }
  
  private def addFamily(f: Family): Unit = {
    if (!families.contains(f))
      families = families :+ f
  }
  
  private def addMember(m: Member): Unit = {
    if (!members.contains(m))
      members = members :+ m
  }
  
  private def addPersons(p: Person): Unit = {
    if (!persons.contains(p))
       persons = persons :+ p
  }
  
  private def addRegister(r: SimplePerson): Unit = {
    if (!registers.contains(r))
      registers = registers :+ r
  }
  
  def setAllFamilyNames(s: String): Unit = {
    families.foreach {
      m => m.setLastName(s)//.setCompleteName(s)
    }
  }
  
  def setAllPersonNames(s: String): Unit = {
    persons.foreach {
      m => m.setFullName(s)
    }
  }
  
  def changeRegisterNames(): Unit = {
    registers.foreach {
      m => m.changeFirstAndLastName()
    }
  }
  
  def setAllRegisterNames(s: String): Unit = {
    registers.foreach {
      m => m.setCompleteName(s)
    }
  }
  
  /**
   * Print all elements from all lists.
   */
  def printALL(): Unit = {
    println("************************************************")
    println("Members: ");
    members.foreach {
      m => println("**Member: " + m);
    }
    println("Families: ");
    families.foreach {
      m => println("**Family: " + m);
    }
    println("Persons: ");
    persons.foreach {
      m => println("**Person: " + m);
    }
    println("Registers: ");
    registers.foreach {
      m => println("**Register: " + m);
    }
    println("************************************************")
  }
}