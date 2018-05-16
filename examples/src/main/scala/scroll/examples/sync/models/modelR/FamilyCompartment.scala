package scroll.examples.sync.models.modelR

import scroll.internal.Compartment

class Human(clastName: String, cfirstName: String, cMale: Boolean) {
  var firstName: String = cfirstName;
  var lastName: String = clastName;
  var male: Boolean = cMale;
}

class FamilyCompartment(cAdress: String) extends Compartment {
  var familyAdress: String = cAdress;

  class ParentRole(cPregnant: Boolean) {
    var pregnant: Boolean = cPregnant;
    var partner: ParentRole = null;
    var kids = List[KidRole]();

    def getPartner(): ParentRole = {
      return partner
    }

    def setPartner(p: ParentRole): Unit = {
      partner = p;
    }

    def getKids(): List[KidRole] = {
      return kids;
    }

    def addKid(k: KidRole): Unit = {
      kids = kids :+ k
    }
  }

  class KidRole() {
    var mother: ParentRole = null;
    var father: ParentRole = null;

    def getMother(): ParentRole = {
      return mother
    }

    def setMother(p: ParentRole): Unit = {
      mother = p
    }

    def getFather(): ParentRole = {
      return father
    }

    def setFather(p: ParentRole): Unit = {
      father = p
    }
  }

}