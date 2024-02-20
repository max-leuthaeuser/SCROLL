package scroll.tests.parameterized

import scroll.tests.mocks._

class RoleConstraintsTest extends AbstractParameterizedSCROLLTest {

  test("Role implication constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA  = new RoleA()
        val roleB  = new RoleB()
        val roleC  = new RoleC()
        roleConstraints.addRoleImplication[RoleA, RoleB]()

        roleConstraints.checked {
          player play roleA play roleB
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player drop roleB
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"

        roleConstraints.checked {
          player play roleB
        }

        roleConstraints.addRoleImplication[RoleB, RoleC]()

        roleConstraints.checked {
          player play roleC
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player drop roleB
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player drop roleC
          }
        } should have message s"Role implication constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"

        roleConstraints.checked {
          player play roleC play roleB
        }

      }
    }
  }

  test("Role prohibition constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA  = new RoleA()
        val roleB  = new RoleB()
        val roleC  = new RoleC()
        roleConstraints.addRoleProhibition[RoleA, RoleB]()

        roleConstraints.checked {
          player play roleA
        }

        roleConstraints.checked {
          player drop roleA
          player play roleB
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player play roleA
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"

        roleConstraints.addRoleProhibition[RoleB, RoleC]()

        roleConstraints.checked {
          player drop roleA
          player drop roleB
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player play roleA
            player play roleB
            player play roleC
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"

        roleConstraints.checked {
          player drop roleB
        }

      }
    }
  }

  test("Role equivalence constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA  = new RoleA()
        val roleB  = new RoleB()
        val roleC  = new RoleC()
        roleConstraints.addRoleEquivalence[RoleA, RoleB]()

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player play roleA
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"

        roleConstraints.checked {
          player play roleB
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player drop roleA
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleA.getClass.getName}', but it does not!"

        roleConstraints.checked {
          player drop roleB
        }

        roleConstraints.addRoleEquivalence[RoleB, RoleC]()

        roleConstraints.checked {
          player play roleA
          player play roleB
          player play roleC
        }

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player drop roleB
          }
        } should have message s"Role equivalence constraint violation: '$player' should play role '${roleB.getClass.getName}', but it does not!"

      }
    }
  }

  test("Role implication and prohibition constraint") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val player = new CoreA()
        val roleA  = new RoleA()
        val roleB  = new RoleB()
        roleConstraints.addRoleImplication[RoleA, RoleB]()
        roleConstraints.addRoleProhibition[RoleA, RoleB]()

        the[RuntimeException] thrownBy {
          roleConstraints.checked {
            player play roleA
            player play roleB
          }
        } should have message s"Role prohibition constraint violation: '$player' plays role '${roleB.getClass.getName}', but it is not allowed to do so!"

      }
    }
  }

}
