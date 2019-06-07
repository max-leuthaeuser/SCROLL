package scroll.tests.parameterized

import scroll.tests.mocks._

class RoleRestrictionsTest extends AbstractParameterizedSCROLLTest {

  test("Validating role restrictions based on role types") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleD = new RoleD()
        AddRoleRestriction[CoreA, RoleA]
        player play roleA
        player drop roleA
        ReplaceRoleRestriction[CoreA, RoleD]
        a[RuntimeException] should be thrownBy {
          player play roleA
        }
      } shouldNot be(null)
    }
  }

  test("Validating role restrictions based on multiple role types") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleD = new RoleD()
        AddRoleRestriction[CoreA, RoleA]
        AddRoleRestriction[CoreA, RoleD]
        player play roleA
        player play roleD
        a[RuntimeException] should be thrownBy {
          player play new RoleB()
        }
      } shouldNot be(null)
    }
  }

  test("Validating role restrictions based on role types when removing restrictions") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val player = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleD = new RoleD()
        AddRoleRestriction[CoreA, RoleA]
        player play roleA
        RemoveRoleRestriction[CoreA]
        player play roleD
        player drop roleA drop roleD
        AddRoleRestriction[CoreA, RoleA]
        AddRoleRestriction[CoreA, RoleD]
        player play roleA play roleD
        player drop roleA drop roleD
        RemoveRoleRestriction[CoreA]
        player play roleA play roleD
      } shouldNot be(null)
    }
  }

}
