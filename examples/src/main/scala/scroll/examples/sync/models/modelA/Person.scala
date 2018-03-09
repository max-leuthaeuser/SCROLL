package scroll.examples.sync.models.modelA

import scroll.examples.sync.PlayerSync

class Person(cname: String) extends PlayerSync {

    var fullName: String = cname
    /*var mother: Female = null;
    var father: Male = null;
    var daughters = List[Female]();
    var sons = List[Male]();*/
    
    buildClass()

    def setFullName(n: String): Unit = {
      fullName = n
      println("++++++++++Per Change+++++++++++++++");
      +this changeFullName ()
      println("----------Per Change---------------");
    }

    def getFullName(): String = {
      return fullName
    }
    
    /*def setFather(f: Male): Unit = {
      father = f
    }
    
    def setMother(m: Female): Unit = {
      mother = m
    }
    
    def addSon(s: Male): Unit = {
      sons = sons :+ s
    }
    
    def addDaughter(d: Female): Unit = {
      daughters = daughters :+ d
    }
    
    def getFather(): Male = {
      return father;
    }
    
    def getMother(): Female = {
      return mother;
    }
    
    def getSons(): List[Male] = {
      return sons;
    }
    
    def getDaughters(): List[Female] = {
      return daughters;
    }*/
    
    def listen(): Unit = {
      println("++++++++++Person+++++++++++++++");
      +this listNames ()
      println("----------Person---------------");
    }

    override def toString(): String = {
      return "Person: " + fullName + " D: " + deleted;
    }  

  }