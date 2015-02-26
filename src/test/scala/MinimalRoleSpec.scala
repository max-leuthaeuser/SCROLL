// removes warnings by Eclipse about reflective calls

import scala.language.reflectiveCalls
import mocks.{CoreB, SomeCompartment, CoreA}
import org.scalatest._

class MinimalRoleSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Dropping compartment and invoking methods") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment {
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
          +someCore a()
        }
        And("binding to RoleB is left untouched of course")
        val resB: String = +someCore b()
        assert(resB == "b")
      }
    }

    scenario("Transferring a role") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("transferring the role")
        someCoreA transfer someRole to someCoreB

        Then("the result of the call to the role of player someCoreB should be correct")
        val res: Int = +someCoreB a()
        assert(res == 0)
        And("a call to the player the role was moved away from should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA a()
        }
      }
    }

    scenario("Handling applyDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method")
        val expected = 0
        val actual: Int = +someCoreA a()

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expected == actual)
        And("a call to the role with a method that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA c()
        }
      }
    }

    scenario("Handling applyDynamicNamed") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method with named params")
        val expected = someRole.b("some", param = "out")
        val actual: String = +someCoreA b("some", param = "out")

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expected == actual)
        And("a call to the role with a method that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA b("some", otherParam = "out")
        }
      }
    }

    scenario("Handling selectDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using selectDynamic to get the value of a role attribute")
        val expectedA = someRole.valueA
        val actualA: String = (+someCoreA).valueA
        val expectedB = someRole.valueB
        val actualB: Int = (+someCoreA).valueB

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expectedA == actualA)
        assert(expectedB == actualB)
        And("a call to the role with a value that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          (+someCoreA).valueC
        }
      }
    }

    scenario("Handling updateDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using updateDynamic to get the value of a role attribute")
        val expectedA = "newValue"
        (+someCoreA).valueA = expectedA
        val actualA: String = (+someCoreA).valueA
        
        val expectedB = -1
        (+someCoreA).valueB = expectedB
        val actualB: Int = (+someCoreA).valueB

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expectedA == actualA)
        assert(expectedB == actualB)
        And("a call to the role with a value that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          (+someCoreA).valueC = "unknown"
        }
      }
    }
  }
}
