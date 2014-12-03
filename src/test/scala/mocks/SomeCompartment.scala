package mocks

import internal.Compartment
import annotations.Role

class SomeCompartment extends Compartment {

  @Role class RoleA {
    def a(): Int =
      {
        println("role a")
        0
      }
  }

  @Role class RoleB {
    def b(): String =
      {
        println("role b")
        "b"
      }
  }

}
