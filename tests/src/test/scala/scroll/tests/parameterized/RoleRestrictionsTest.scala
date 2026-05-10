package scroll.tests.parameterized

import scroll.internal.errors.SCROLLErrors.RoleRestrictionViolation
import scroll.tests.mocks._

class RoleRestrictionsTest extends AbstractParameterizedSCROLLTest {

  test("Validating role restrictions based on role types") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleD = new RoleD()
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        player play roleA
        player drop roleA
        roleRestrictions.replaceRoleRestriction[CoreA, RoleD]()

        val violation = the[RoleRestrictionViolation] thrownBy {
          player play roleA
        }
        violation.player shouldBe player
        violation.role shouldBe roleA

      }
    }
  }

  test("Validating role restrictions based on multiple role types") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleD = new RoleD()
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        roleRestrictions.addRoleRestriction[CoreA, RoleD]()
        player play roleA
        player play roleD

        val violation = the[RoleRestrictionViolation] thrownBy {
          player play roleB
        }
        violation.player shouldBe player
        violation.role shouldBe roleB

      }
    }
  }

  test("Validating role restrictions based on role types when removing restrictions") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleD = new RoleD()
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        player play roleA
        roleRestrictions.removeRoleRestriction[CoreA]()
        player play roleD
        player drop roleA drop roleD
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        roleRestrictions.addRoleRestriction[CoreA, RoleD]()
        player play roleA play roleD
        player drop roleA drop roleD
        roleRestrictions.removeRoleRestriction[CoreA]()
        player play roleA play roleD
      }
    }
  }

}
