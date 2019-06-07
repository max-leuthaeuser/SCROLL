package scroll.tests.parameterized

import scroll.tests.mocks._

class RoleConstraintsTest extends AbstractParameterizedSCROLLTest {

  test("Role implication constraint") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        RoleImplication[RoleA, RoleB]()
        RoleConstraintsChecked {
          player play roleA play roleB
        }
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }
        RoleConstraintsChecked {
          player play roleB
        }
        RoleImplication[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleC
        }
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleC
          }
        }
        RoleConstraintsChecked {
          player play roleC play roleB
        }
      } shouldNot be(null)
    }
  }

  test("Role prohibition constraint") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
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
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        }
        RoleProhibition[RoleB, RoleC]()
        RoleConstraintsChecked {
          player drop roleA
          player drop roleB
        }
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
            player play roleC
          }
        }
        RoleConstraintsChecked {
          player drop roleB
        }
      } shouldNot be(null)
    }
  }

  test("Role equivalence constraint") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        RoleEquivalence[RoleA, RoleB]()
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        }
        RoleConstraintsChecked {
          player play roleB
        }
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleA
          }
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
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }
      } shouldNot be(null)
    }
  }

  test("Role implication and prohibition constraint") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        RoleImplication[RoleA, RoleB]()
        RoleProhibition[RoleA, RoleB]()
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
          }
        }
      } shouldNot be(null)
    }
  }

}
