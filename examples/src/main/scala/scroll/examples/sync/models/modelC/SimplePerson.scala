package scroll.examples.sync.models.modelC

import scroll.examples.sync.PlayerSync

/**
 * SimplePerson from model C.
 */
class SimplePerson(ccompleteName: String, cmale: Boolean) extends PlayerSync {

  var completeName: String = ccompleteName
  var knows = List[SimplePerson]()
  var address: String = null
  var male: Boolean = cmale

  buildClass()

  def getMale(): Boolean = {
    return male;
  }

  def setAddress(a: String): Unit = {
    address = a
  }

  def getAddress(): String = {
    return address
  }

  def addKnown(p: SimplePerson): Unit = {
    if (!knows.contains(p) && p != this) {
      println("Add Known");
      knows = knows :+ p
    }
  }

  def setCompleteName(n: String): Unit = {
    completeName = n
    println("++++++++++Reg Change+++++++++++++++");
    +this changeCompleteName ()
    println("----------Reg Change---------------");
  }

  def getCompleteName(): String = {
    return completeName
  }

  def changeFirstAndLastName(): Unit = {
    var result: Array[java.lang.String] = completeName.split(" ");
    if (result.size > 1) {
      completeName = result.last + "," + result.head
    } else {
      var result2: Array[java.lang.String] = completeName.split(",");
      completeName = result2.last + " " + result2.head
    }
  }

  override def toString(): String = {
    return "Register: " + completeName + " M: " + male + " Kn: " + knows.size + " D: " + deleted;
  }

}