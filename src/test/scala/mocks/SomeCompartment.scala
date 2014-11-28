package mocks

import internal.Compartment

class SomeCompartment extends Compartment {

  class RoleA {
    def a(): Int =
      {
        println("role a")
        0
      }
  }

  class RoleB {
    def b(): String =
      {
        println("role b")
        "b"
      }
  }

}
