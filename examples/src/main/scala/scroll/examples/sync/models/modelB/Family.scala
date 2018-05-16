package scroll.examples.sync.models.modelB

import scroll.examples.sync.PlayerSync

/**
  * Family from model B.
  */
class Family(clastName: String) extends PlayerSync {

  var lastName: String = clastName;
  var father: Member = null;
  var mother: Member = null;
  var sons = List[Member]();
  var daughters = List[Member]();

  buildClass()

  //father.setFamilyFather(this)
  //mother.setFamilyMother(this)

  def setLastName(n: String): Unit = {
    lastName = n
    println("++++++++++Family Change+++++++++++++++");
    +this changeLastName()
    println("----------Family Change---------------");
  }

  def getFather(): Member = {
    return father
  }

  def getMother(): Member = {
    return mother
  }

  def getSons(): List[Member] = {
    return sons
  }

  def getDaughters(): List[Member] = {
    return daughters
  }

  def setFather(f: Member): Unit = {
    father = f
    +this changeFather()
  }

  def setMother(m: Member): Unit = {
    mother = m
    +this changeMother()
  }

  def addSon(s: Member): Unit = {
    sons = sons :+ s
    +this changeSon()
  }

  def addDaughter(d: Member): Unit = {
    daughters = daughters :+ d
    +this changeDaughter()
  }

  def getLastName(): String = {
    return lastName
  }

  override def toString(): String = {
    return "Family: " + lastName + " D: " + deleted;
  }

  def listen(): Unit = {
    println("++++++++++Family+++++++++++++++");
    +this listNames()
    println("----------Family---------------");
  }
}