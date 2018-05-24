package scroll.tests

import mocks.{CoreA, SomeCompartment}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class EqualityRoleTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role equality.")

  Feature("Role playing equality") {
    Scenario("Player and Role equality (flat roles)") {
      Given("some player and a role in a compartment")

      val someCore = new CoreA()
      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        val player = someCore play someRole

        When("comparing identity between core and player")
        Then("player and core should have the same identity")
        player shouldBe player
        someCore shouldBe someCore
        player shouldBe someCore

        When("comparing core and core playing a role")
        Then("They should have the same identity")
        (+player) shouldBe player
        player shouldBe (+player)

        When("comparing a role to itself")
        Then("it should have the same identity")
        someRole shouldBe someRole

        When("comparing a role core to the player")
        Then("it should have the same identity")
        (+someRole) shouldBe player
        player shouldBe (+someRole)

        When("comparing a role core to the core")
        Then("it should have the same identity")
        (+someRole) shouldBe someCore
      }
    }

    Scenario("Player and Role equality (deep roles)") {
      Given("some player and roles in a compartment")

      val someCore = new CoreA()
      new SomeCompartment() {
        val someRole = new RoleA()
        val someOtherRole = new RoleB()
        And("some play relationships")
        val player = (someCore play someRole) play someOtherRole

        When("comparing identity between core and player")
        Then("player and core should have the same identity")
        player shouldBe player
        someCore shouldBe someCore
        player shouldBe someCore

        When("comparing core and core playing a role")
        Then("They should have the same identity")
        (+player) shouldBe player
        player shouldBe (+player)

        When("comparing a role to itself")
        Then("it should have the same identity")
        someRole shouldBe someRole
        someOtherRole shouldBe someOtherRole

        When("comparing different roles")
        Then("they should not equal")
        someRole.hashCode() should not be someOtherRole.hashCode()

        When("comparing a role core to the player")
        Then("it should have the same identity")
        val a = +someRole
        val b = +someOtherRole
        a shouldBe player
        player shouldBe a
        b shouldBe player
        player shouldBe b

        When("comparing a role core to the core")
        Then("it should have the same identity")
        (+someRole) shouldBe someCore
        (+someOtherRole) shouldBe someCore
      }
    }
  }
}
