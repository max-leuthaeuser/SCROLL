package scroll.tests.cached

import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks._

class RoleRestrictionsTest extends AbstractSCROLLTest {
  info(s"Test spec for role restrictions with cache = '$cached'.")

  Feature("Specifying role restrictions") {
    Scenario("Validating role restrictions based on role types") {
      Given("A natural, some role instances")
      val player = new CoreA()
      When("A role restriction is specified")
      new CompartmentUnderTest() {
        val roleA = new RoleA()
        val roleD = new RoleD()
        And("some role type specifications are given")
        AddRoleRestriction[CoreA, RoleA]

        Then("All role restriction should hold")
        player play roleA

        player drop roleA
        When("A role restriction is specified that could not be hold")
        ReplaceRoleRestriction[CoreA, RoleD]
        Then("A runtime exception is expected")
        a[RuntimeException] should be thrownBy {
          player play roleA
        }
      }
    }

    Scenario("Validating role restrictions based on multiple role types") {
      Given("A natural, some role instances")
      val player = new CoreA()
      When("Multiple role restrictions are specified")
      new CompartmentUnderTest() {
        val roleA = new RoleA()
        val roleD = new RoleD()
        AddRoleRestriction[CoreA, RoleA]
        AddRoleRestriction[CoreA, RoleD]

        Then("All role restriction should hold")
        player play roleA
        player play roleD

        When("A role restriction is specified that could not be hold")
        Then("A runtime exception is expected")
        a[RuntimeException] should be thrownBy {
          player play new RoleB()
        }
      }
    }

    Scenario("Validating role restrictions based on role types when removing restrictions") {
      Given("A natural, some role instances")
      val player = new CoreA()
      When("A role restriction is specified")
      new CompartmentUnderTest() {
        val roleA = new RoleA()
        val roleD = new RoleD()
        And("some role type specifications are given")
        AddRoleRestriction[CoreA, RoleA]

        Then("All role restriction should hold")
        player play roleA

        When("A role restiction is removed")
        RemoveRoleRestriction[CoreA]
        Then("Role playing should be fine")
        player play roleD
        player drop roleA drop roleD

        And("Also in the case of multiple restriction that are removed later on")
        AddRoleRestriction[CoreA, RoleA]
        AddRoleRestriction[CoreA, RoleD]
        player play roleA play roleD
        player drop roleA drop roleD
        RemoveRoleRestriction[CoreA]
        player play roleA play roleD
      }
    }
  }
}
