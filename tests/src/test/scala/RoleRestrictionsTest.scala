import mocks.{CoreA, SomeCompartment}
import org.scalatest.concurrent.AsyncAssertions._
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.rpa.RolePlayingAutomaton
import scroll.internal.rpa.RolePlayingAutomaton._

class RoleRestrictionsTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role restrictions.")

  feature("Specifying role restrictions") {
    scenario("Validating role restrictions") {
      Given("A natural, some role instances")
      val player = new CoreA()
      When("A role restriction is specified")
      new SomeCompartment {
        val roleA = new RoleA()
        And("some role type specifications are given")
        type RoleTypeA = {def a(): Int}
        type RoleTypeB = {def a(i: Int, s: String): Int}
        RoleRestriction[CoreA, RoleTypeA]

        Then("All role restriction should hold")
        player play roleA

        When("A restriction is not met")
        player drop roleA
        RoleRestriction[CoreA, RoleTypeB]
        Then("A runtime exception is expected")
        a[RuntimeException] should be thrownBy {
          player play roleA
        }
      }
    }
  }
}
