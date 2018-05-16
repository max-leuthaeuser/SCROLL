package scroll.examples.sync.models.modelB

import scroll.examples.sync.PlayerSync

/**
  * Member from model B.
  */
class Member(cfirstName: String, cfamily: Family, cf: Boolean, cm: Boolean, cs: Boolean, cd: Boolean) extends PlayerSync {

  var firstName: String = cfirstName
  var familyFather: Family = null;
  var familyMother: Family = null;
  var familyDaughter: Family = null;
  var familySon: Family = null;

  if (cf) {
    familyFather = cfamily
    //cfamily.father = this
  } else if (cm) {
    familyMother = cfamily
    //cfamily.mother = this
  } else if (cs) {
    familySon = cfamily
    //cfamily.sons = cfamily.sons :+ this
  } else if (cd) {
    familyDaughter = cfamily
    //cfamily.daughters = cfamily.daughters :+ this
  }

  def setFirstName(n: String): Unit = {
    firstName = n
    println("++++++++++Member Change+++++++++++++++");
    +this changeFirstName()
    println("----------Member Change---------------");
  }

  def getLastName(): String = {
    if (familyFather != null)
      return familyFather.getLastName();
    if (familyMother != null)
      return familyMother.getLastName();
    if (familySon != null)
      return familySon.getLastName();
    if (familyDaughter != null)
      return familyDaughter.getLastName();
    return "";
  }

  def getFirstName(): String = {
    return firstName
  }

  def getFamilyFather(): Family = {
    return familyFather
  }

  def getFamilyMother(): Family = {
    return familyMother
  }

  def getFamilyDaughter(): Family = {
    return familyDaughter
  }

  def getFamilySon(): Family = {
    return familySon
  }

  //Diese Funktionen sollten nicht genutzt werden da in der Family das alles gesetzt werden kann
  /*def setFamilyFather(m: Family): Unit = {
      familyFather = m
    }
    
    def setFamilyMother(m: Family): Unit = {
      familyMother = m
    }
    
    def setFamilySon(m: Family): Unit = {
      familySon = m
    }
    
    def setFamilyDaughter(m: Family): Unit = {
      familyDaughter = m
    }*/

  override def toString(): String = {
    if (familyFather != null)
      return "Member: " + firstName + " " + familyFather.getLastName() + " D: " + deleted;
    if (familyMother != null)
      return "Member: " + firstName + " " + familyMother.getLastName() + " D: " + deleted;
    if (familySon != null)
      return "Member: " + firstName + " " + familySon.getLastName() + " D: " + deleted;
    if (familyDaughter != null)
      return "Member: " + firstName + " " + familyDaughter.getLastName() + " D: " + deleted;
    return "Member: " + firstName + " D: " + deleted;
  }

  def listen(): Unit = {
    println("++++++++++Member+++++++++++++++");
    +this listNames()
    println("----------Member---------------");
  }

  buildClass()

  if (cf) {
    cfamily.setFather(this)
  } else if (cm) {
    cfamily.setMother(this)
  } else if (cs) {
    cfamily.addSon(this)
  } else if (cd) {
    cfamily.addDaughter(this)
  }
}