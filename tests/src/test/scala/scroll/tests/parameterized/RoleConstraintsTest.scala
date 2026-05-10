package scroll.tests.parameterized

import scroll.internal.errors.SCROLLErrors.RoleEquivalenceConstraintViolation
import scroll.internal.errors.SCROLLErrors.RoleImplicationConstraintViolation
import scroll.internal.errors.SCROLLErrors.RoleProhibitionConstraintViolation
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

        val implicationViolation1 = the[RoleImplicationConstraintViolation] thrownBy
          roleConstraints.checked {
            player drop roleB
          }
        implicationViolation1.player shouldBe player
        implicationViolation1.requiredRole shouldBe roleB.getClass.getName

        roleConstraints.checked {
          player play roleB
        }

        roleConstraints.addRoleImplication[RoleB, RoleC]()

        roleConstraints.checked {
          player play roleC
        }

        val implicationViolation2 = the[RoleImplicationConstraintViolation] thrownBy
          roleConstraints.checked {
            player drop roleB
          }
        implicationViolation2.player shouldBe player
        implicationViolation2.requiredRole shouldBe roleB.getClass.getName

        val implicationViolation3 = the[RoleImplicationConstraintViolation] thrownBy
          roleConstraints.checked {
            player drop roleC
          }
        implicationViolation3.player shouldBe player
        implicationViolation3.requiredRole shouldBe roleB.getClass.getName

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

        val prohibitionViolation1 = the[RoleProhibitionConstraintViolation] thrownBy
          roleConstraints.checked {
            player play roleA
          }
        prohibitionViolation1.player shouldBe player
        prohibitionViolation1.prohibitedRole shouldBe roleB.getClass.getName

        roleConstraints.addRoleProhibition[RoleB, RoleC]()

        roleConstraints.checked {
          player drop roleA
          player drop roleB
        }

        val prohibitionViolation2 = the[RoleProhibitionConstraintViolation] thrownBy
          roleConstraints.checked {
            player play roleA
            player play roleB
            player play roleC
          }
        prohibitionViolation2.player shouldBe player
        prohibitionViolation2.prohibitedRole shouldBe roleB.getClass.getName

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

        val equivalenceViolation1 = the[RoleEquivalenceConstraintViolation] thrownBy
          roleConstraints.checked {
            player play roleA
          }
        equivalenceViolation1.player shouldBe player
        equivalenceViolation1.requiredRole shouldBe roleB.getClass.getName

        roleConstraints.checked {
          player play roleB
        }

        val equivalenceViolation2 = the[RoleEquivalenceConstraintViolation] thrownBy
          roleConstraints.checked {
            player drop roleA
          }
        equivalenceViolation2.player shouldBe player
        equivalenceViolation2.requiredRole shouldBe roleA.getClass.getName

        roleConstraints.checked {
          player drop roleB
        }

        roleConstraints.addRoleEquivalence[RoleB, RoleC]()

        roleConstraints.checked {
          player play roleA
          player play roleB
          player play roleC
        }

        val equivalenceViolation3 = the[RoleEquivalenceConstraintViolation] thrownBy
          roleConstraints.checked {
            player drop roleB
          }
        equivalenceViolation3.player shouldBe player
        equivalenceViolation3.requiredRole shouldBe roleB.getClass.getName

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

        val prohibitionViolation = the[RoleProhibitionConstraintViolation] thrownBy
          roleConstraints.checked {
            player play roleA
            player play roleB
          }
        prohibitionViolation.player shouldBe player
        prohibitionViolation.prohibitedRole shouldBe roleB.getClass.getName

      }
    }
  }

}
