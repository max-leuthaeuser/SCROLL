package scroll.examples.sync.models.modelA

import scroll.examples.sync.PlayerSync

/**
  * General person from model A.
  */
abstract class Person(cname: String) extends PlayerSync {

  var fullName: String = cname

  buildClass()

  def setFullName(n: String): Unit = {
    fullName = n
    println("++++++++++Per Change+++++++++++++++");
    +this changeFullName()
    println("----------Per Change---------------");
  }

  def getFullName(): String = {
    return fullName
  }

  def listen(): Unit = {
    println("++++++++++Person+++++++++++++++");
    +this listNames()
    println("----------Person---------------");
  }

  override def toString(): String = {
    return "Person: " + fullName + " D: " + deleted;
  }

}