package scroll.examples.sync

import scroll.examples.sync.models.modelB.Family
import scroll.examples.sync.models.modelB.Member
import scroll.examples.sync.models.modelA.Person
import scroll.examples.sync.models.modelC.SimplePerson

object ModelElementLists {
  
  var families = List[Family]()
  var members = List[Member]()
  var persons = List[Person]()
  var registers = List[SimplePerson]() 
  
  def addFamily(f: Family): Unit = {
    if (!families.contains(f))
      families = families :+ f
  }
  
  def addMember(m: Member): Unit = {
    if (!members.contains(m))
      members = members :+ m
  }
  
  def addPersons(p: Person): Unit = {
    if (!persons.contains(p))
       persons = persons :+ p
  }
  
  def addRegister(r: SimplePerson): Unit = {
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
  
  def printALL(): Unit = {
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
  }
}