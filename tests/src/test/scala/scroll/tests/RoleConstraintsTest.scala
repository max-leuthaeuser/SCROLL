package scroll.tests

import org.junit.Test
import org.junit.Assert.fail

import mocks.{CoreA, SomeCompartment}

class RoleConstraintsTest {

  @Test
  def testRoleImplication(): Unit = {
    new SomeCompartment() {
      val player = new CoreA()
      val roleA = new RoleA()
      val roleB = new RoleB()
      val roleC = new RoleC()
      RoleImplication[RoleA, RoleB]()
      RoleConstraintsChecked {
        player play roleA play roleB
      }

      try {
        RoleConstraintsChecked {
          player drop roleB
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleConstraintsChecked {
        player play roleB
      }

      RoleImplication[RoleB, RoleC]()
      RoleConstraintsChecked {
        player play roleC
      }

      try {
        RoleConstraintsChecked {
          player drop roleB
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      try {
        RoleConstraintsChecked {
          player drop roleC
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleConstraintsChecked {
        player play roleC play roleB
      }
    }
  }

  @Test
  def testRoleProhibition(): Unit = {
    new SomeCompartment() {
      val player = new CoreA()
      val roleA = new RoleA()
      val roleB = new RoleB()
      val roleC = new RoleC()
      RoleProhibition[RoleA, RoleB]()
      RoleConstraintsChecked {
        player play roleA
      }

      RoleConstraintsChecked {
        player drop roleA
        player play roleB
      }

      try {
        RoleConstraintsChecked {
          player play roleA
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleProhibition[RoleB, RoleC]()
      RoleConstraintsChecked {
        player drop roleA
        player drop roleB
      }

      try {
        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleConstraintsChecked {
        player drop roleB
      }
    }
  }

  @Test
  def testRoleEquivalence(): Unit = {
    new SomeCompartment() {
      val player = new CoreA()
      val roleA = new RoleA()
      val roleB = new RoleB()
      val roleC = new RoleC()
      RoleEquivalence[RoleA, RoleB]()

      try {
        RoleConstraintsChecked {
          player play roleA
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleConstraintsChecked {
        player play roleB
      }

      try {
        RoleConstraintsChecked {
          player drop roleA
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }

      RoleConstraintsChecked {
        player drop roleB
      }

      RoleEquivalence[RoleB, RoleC]()
      RoleConstraintsChecked {
        player play roleA
        player play roleB
        player play roleC
      }

      try {
        RoleConstraintsChecked {
          player drop roleB
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testMixedRoleConstraints(): Unit = {
    new SomeCompartment() {
      val player = new CoreA()
      val roleA = new RoleA()
      val roleB = new RoleB()
      RoleImplication[RoleA, RoleB]()
      RoleProhibition[RoleA, RoleB]()

      try {
        RoleConstraintsChecked {
          player play roleA
          player play roleB
        }
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }
}
