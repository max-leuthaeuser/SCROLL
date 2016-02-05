package scroll.tests.mocks

import scroll.internal.annotations.Role
import scroll.internal.Compartment
import scroll.internal.graph.{ScalaRoleGraph, CachedScalaRoleGraph}

class SomeCompartment(val cached: Boolean) extends Compartment {

  plays = cached match {
    case true => new CachedScalaRoleGraph()
    case false => new ScalaRoleGraph()
  }

  @Role class RoleA {
    val valueA: String = "valueA"
    var valueB: Int = 1

    var valueC: String = "valueC"

    def a(): Int = {
      0
    }

    def b(a: String, param: String = "in"): String = param

    def update(value: String) {
      this.valueC = value
    }
  }

  @Role class RoleB {
    def b(): String = {
      "b"
    }
  }

  @Role class RoleC {
    def unionTypedMethod[T: (Int or String)#λ](param: T): Int = param match {
      case i: Int => i
      case s: String => s.length
    }
  }

  @Role class RoleD {
    var valueA: String = "valueA"
    var valueB: Int = -1

    def update(vA: String, vB: Int) {
      valueA = vA
      valueB = vB
    }
  }

  @Role class RoleE {
    var valueInt: Int = -1

    var valueDouble: Double = -1

    var valueFloat: Float = -1

    var valueLong: Long = -1

    var valueShort: Short = -1

    var valueByte: Byte = -1

    var valueChar: Char = 'A'

    var valueBoolean: Boolean = false

    def updateInt(newValue: Int) {
      valueInt = newValue
    }

    def updateDouble(newValue: Double) {
      valueDouble = newValue
    }

    def updateFloat(newValue: Float) {
      valueFloat = newValue
    }

    def updateLong(newValue: Long) {
      valueLong = newValue
    }

    def updateShort(newValue: Short) {
      valueShort = newValue
    }

    def updateByte(newValue: Byte) {
      valueByte = newValue
    }

    def updateChar(newValue: Char) {
      valueChar = newValue
    }

    def updateBoolean(newValue: Boolean) {
      valueBoolean = newValue
    }

  }

}
