import mocks.{CoreB, SomeCompartment, CoreA}
import org.scalatest._

class MinimalRoleSpec extends FeatureSpec with GivenWhenThen with Matchers
{
  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Attach some compartment to players and invoke methods") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment
      {
        val someRole = new RoleA()

        When("call to core object")
        someCore.a()
        And("defining play relationship")
        And("calling methods on the resulting dynamic type")
        val resA: Int = (someCore play someRole).a()
        val resB: String = ((someCore play someRole) play new RoleB()).b()

        Then("role method invocation results should be correctly")
        assert(resA == 0)
        assert(resB.equals("b"))
      }
    }

    scenario("Invoking methods without explicit notion of compartment") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment
      {
        val someRole = new RoleA()

        And("a play relationship")
        someCore play someRole

        When("call method on core")
        val resA: Int = ~someCore a()
        And("call method on role with base link")
        !someRole a()

        Then("return value of role call should be correct")
        assert(resA == 0)
      }
    }

    scenario("Dropping compartment and invoking methods") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment
      {
        val someRole = new RoleA()
        And("a play relationship")
        someCore play someRole
        someCore play new RoleB()

        When("dropping the role")
        someCore drop someRole

        Then("the call must be invoked on the core object")
        someCore a()
        And("a role call should fail")
        a[RuntimeException] should be thrownBy {
          ~someCore a()
        }
        And("binding to RoleB is left untouched of course")
        val resB: String = ~someCore b()
        assert(resB.equals("b"))
      }
    }

    scenario("Transferring a role") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new SomeCompartment
      {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("transferring the role")
        someCoreA transfer someRole to someCoreB

        Then("the result of the call to the role of player someCoreB should be correct")
        val res: Int = ~someCoreB a()
        assert(res == 0)
        And("a call to the player the role was moved away from should fail")
        a[RuntimeException] should be thrownBy {
          ~someCoreA a()
        }
      }
    }

    // TODO: add test for role/player equality
  }
}
