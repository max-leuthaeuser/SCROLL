import mocks.{ SomeCompartment, CoreA }
import org.scalatest.{ Matchers, GivenWhenThen, FeatureSpec }

class EqualityRoleSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role equality.")

  feature("Role playing equality") {
    scenario("Player and Role equality (flat roles)") {
      Given("some player and a role in a compartment")

      val someCore = new CoreA()
      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        val player = someCore play someRole

        When("comparing identity between core and player")
        Then("player and core should have the same identity")
        assert(player == player)
        assert(someCore == someCore)
        // TODO: equals of Any doesn't compare to Player
        // assert(someCore == player /* .core */ )
        assert(player == someCore)

        When("comparing core and core playing a role")
        Then("They should have the same identity")
        assert((+player) == player)
        assert(player == (+player))

        When("comparing a role to itself")
        Then("it should have the same identity")
        assert(someRole == someRole)

        When("comparing a role core to the player")
        Then("it should have the same identity")
        val a = -someRole
        assert((-someRole) == player)
        assert(player == (-someRole))

        When("comparing a role core to the core")
        Then("it should have the same identity")
        // TODO: equals of Any doesn't compare to Role
        assert((-someRole) == someCore)
      }
    }

    scenario("Player and Role equality (deep roles)") {
      Given("some player and roles in a compartment")

      val someCore = new CoreA()
      new SomeCompartment {
        val someRole = new RoleA()
        val someOtherRole = new RoleB()
        And("some play relationships")
        val player = (someCore play someRole) play someOtherRole

        When("comparing identity between core and player")
        Then("player and core should have the same identity")
        assert(player == player)
        assert(someCore == someCore)
        assert(player == someCore)

        When("comparing core and core playing a role")
        Then("They should have the same identity")
        assert((+player) == player)
        assert(player == (+player))

        When("comparing a role to itself")
        Then("it should have the same identity")
        assert(someRole == someRole)
        assert(someOtherRole == someOtherRole)

        When("comparing different roles")
        Then("they should not equal")
        assertResult(false)(someRole.hashCode() == someOtherRole.hashCode())
        assertResult(false)(someOtherRole.hashCode() == someRole.hashCode())

        When("comparing a role core to the player")
        Then("it should have the same identity")
        val a = -someRole
        val b = -someOtherRole
        assert(a == player)
        assert(player == a)
        assert(b == player)
        assert(player == b)

        When("comparing a role core to the core")
        Then("it should have the same identity")
        assert((-someRole) == someCore)
        assert((-someOtherRole) == someCore)
      }
    }
  }
}
