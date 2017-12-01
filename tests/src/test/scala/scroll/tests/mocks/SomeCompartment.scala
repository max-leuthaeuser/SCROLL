package scroll.tests.mocks

import scroll.internal.Compartment

class SomeCompartment extends Compartment {

  class RoleA {
    val valueA: String = "valueA"
    var valueB: Int = 1

    var valueC: String = "valueC"

    def a(): Int = {
      0
    }

    def b(a: String, param: String = "in"): String = param

    def update(value: String): Unit = {
      this.valueC = value
    }
  }

  class RoleB {
    def b(): String = {
      "b"
    }
  }

  class RoleC {
    def unionTypedMethod[T: (Int or String)#λ](param: T): Int = param match {
      case i: Int => i
      case s: String => s.length
    }
  }

  class RoleD {
    var valueA: String = "valueA"
    var valueB: Int = -1

    def update(vA: String, vB: Int): Unit = {
      valueA = vA
      valueB = vB
    }
  }

  class RoleE {
    var valueInt: Int = -1

    var valueDouble: Double = -1

    var valueFloat: Float = -1

    var valueLong: Long = -1

    var valueShort: Short = -1

    var valueByte: Byte = -1

    var valueChar: Char = 'A'

    var valueBoolean: Boolean = false

    def updateInt(newValue: Int): Unit = {
      valueInt = newValue
    }

    def updateDouble(newValue: Double): Unit = {
      valueDouble = newValue
    }

    def updateFloat(newValue: Float): Unit = {
      valueFloat = newValue
    }

    def updateLong(newValue: Long): Unit = {
      valueLong = newValue
    }

    def updateShort(newValue: Short): Unit = {
      valueShort = newValue
    }

    def updateByte(newValue: Byte): Unit = {
      valueByte = newValue
    }

    def updateChar(newValue: Char): Unit = {
      valueChar = newValue
    }

    def updateBoolean(newValue: Boolean): Unit = {
      valueBoolean = newValue
    }

  }

}
