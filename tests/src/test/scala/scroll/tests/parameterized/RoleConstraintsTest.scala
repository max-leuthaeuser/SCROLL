package scroll.tests.parameterized

import scroll.tests.mocks._

class RoleConstraintsTest extends AbstractParameterizedSCROLLTest {

  test("Role implication constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        RoleImplication[RoleA, RoleB]()
        RoleConstraintsChecked {
          player play roleA play roleB
        }
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"
        RoleConstraintsChecked {
          player play roleB
        }
        RoleImplication[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleC
        }
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player drop roleC
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"
        RoleConstraintsChecked {
          player play roleC play roleB
        }
      } shouldNot be(null)
    }
  }

  test("Role prohibition constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
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
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"
        RoleProhibition[RoleB, RoleC]()
        RoleConstraintsChecked {
          player drop roleA
          player drop roleB
        }
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
            player play roleC
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"
        RoleConstraintsChecked {
          player drop roleB
        }
      } shouldNot be(null)
    }
  }

  test("Role equivalence constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        RoleEquivalence[RoleA, RoleB]()
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"
        RoleConstraintsChecked {
          player play roleB
        }
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player drop roleA
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleA.getClass.getName}', but it does not!"
        RoleConstraintsChecked {
          player drop roleB
        }
        RoleEquivalence[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        }
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"
      } shouldNot be(null)
    }
  }

  test("Role implication and prohibition constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        RoleImplication[RoleA, RoleB]()
        RoleProhibition[RoleA, RoleB]()
        the [RuntimeException] thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"
      } shouldNot be(null)
    }
  }

}
