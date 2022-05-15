package scroll.tests.parameterized

import scroll.tests.mocks._

class RoleRestrictionsTest extends AbstractParameterizedSCROLLTest {

  test("Validating role restrictions based on role types") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA
        val roleD = new RoleD
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        player play roleA
        player drop roleA
        roleRestrictions.replaceRoleRestriction[CoreA, RoleD]()
        the[RuntimeException] thrownBy {
          player play roleA
        } should have message s"Role '$roleA' can not be played by '$player' due to the active role restrictions!"
      }
    }
  }

  test("Validating role restrictions based on multiple role types") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA
        val roleB = new RoleB
        val roleD = new RoleD
        roleRestrictions.addRoleRestriction[CoreA, RoleA]()
        roleRestrictions.addRoleRestriction[CoreA, RoleD]()
        player play roleA
        player play roleD
        the[RuntimeException] thrownBy {
          player play roleB
        } should have message s"Role '$roleB' can not be played by '$player' due to the active role restrictions!"
      }
    }
  }

  test("Validating role restrictions based on role types when removing restrictions") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val player = new CoreA
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA
        val roleD = new RoleD
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
