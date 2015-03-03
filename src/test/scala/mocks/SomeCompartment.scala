package mocks

import internal.Compartment
import annotations.Role

class SomeCompartment extends Compartment {

  @Role class RoleA {
    val valueA: String = "valueA"
    var valueB: Int = 1
    
    var valueC: String = "valueC"
    
    def a(): Int =
      {
        println("role a")
        0
      }
    
    def b(a: String, param: String = "in"): String = param
    
    def update(value: String) {
      this.valueC = value
    }
  }

  @Role class RoleB {
    def b(): String =
      {
        println("role b")
        "b"
      }
  }

  @Role class RoleC {
    def unionTypedMethod[T: (Int or String)#λ](param: T) = param match {
      case i: Int => i
      case s: String => s.length
    }
  }

}
