// removes warnings by Eclipse about reflective calls

import internal.DispatchQuery._
import internal.util.Log

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
          (+someCoreA).valueUnkown = "unknown"
        }
      }
    }

    scenario("Playing a role multiple times (same instance)") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole
        someCoreA play someRole

        When("updating role attributes")
        val expected = "updated"
        (+someCoreA).update(expected)

        val actual1: String = someRole.valueC
        val actual2: String = (+someCoreA).valueC

        Then("the role and player instance should be updated correctly.")
        assert(expected == actual1)
        assert(expected == actual2)
      }
    }

    scenario("Playing a role multiple times (different instances)") {
      Given("some players and 2 role instance of the same type in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        And("a play relationship")
        someCoreA play someRole1
        someCoreA play someRole2

        When("updating role attributes")
        val expected = "updated"
        (+someCoreA).update(expected)

        val actual1a: String = someRole1.valueC
        val actual1b: String = someRole2.valueC
        val actual2: String = (+someCoreA).valueC

        Then("one role and the player instance should be updated correctly.")
        assert(expected == actual1a || expected == actual1b)
        assert(expected == actual2)
      }
    }

    scenario("Playing a role multiple times (different instances, but using dispatch to select one)") {
      Given("some players and 2 role instance of the same type in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        someRole1.valueB = 1
        someRole2.valueB = 2
        And("a play relationship")
        someCoreA play someRole1
        someCoreA play someRole2

        When("updating role attributes")

        implicit var dd = From(_.isInstanceOf[CoreA]).
          To(_.isInstanceOf[RoleA]).
          Through(_ => true).
          Bypassing({
            case r: RoleA => 1 == r.valueB // so we ignore someRole1 here while dispatching the call to update
            case _ => false
          })

        (+someCoreA).update("updated")

        val actual1: String = someRole1.valueC
        val actual2: String = someRole2.valueC
        val actual3: String = (+someCoreA).valueC

        Then("one role and the player instance should be updated correctly.")
        assert("valueC" == actual1)
        assert("updated" == actual2)
        assert("updated" == actual3)
      }
    }
  }
}
