package mocks

import internal.Compartment
import annotations.Role

class SomeCompartment extends Compartment {

  @Role class RoleA {
    val valueA: String = "valueA"
    val valueB: Int = 1
    
    def a(): Int =
      {
        println("role a")
        0
      }
    
    def b(a: String, param: String = "in"): String = param
  }

  @Role class RoleB {
    def b(): String =
      {
        println("role b")
        "b"
      }
  }

}
