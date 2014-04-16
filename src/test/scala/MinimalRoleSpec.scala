import org.scalatest._
import players._
import roles._
import players.PlayerConversion._
import roles.RoleConversions._

class MinimalRoleSpec extends FeatureSpec with GivenWhenThen with Matchers
{
  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Attach some roles to players and invoke methods") {
      Given("some player and role")
      val someCore = new CoreA()
      // TODO: ensure roles can't be instantiated
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
      println(RoleManager.plays)
    }

    scenario("Invoking methods without explicit notion of roles") {
      Given("some player and role")
      val someCore = new CoreA()
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

    scenario("Dropping roles and invoking methods") {
      Given("some player and role")
      val someCore = new CoreA()
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
}
