package scroll.tests

import org.scalatest._
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._
import scroll.tests.mocks.{CoreA, SomeCompartment}

class RoleSortingTest extends FeatureSpec with GivenWhenThen with Matchers {

  info("Test spec for sorting dynamic extensions.")

  feature("Role sorting") {
    scenario("Adding roles and sorting them") {
      Given("some player and roles in a compartment")
      val someCore = new CoreA()
      new SomeCompartment() {

        case class SomeRoleA() {
          def method(): String = "A"
        }

        case class SomeRoleB() {
          def method(): String = "B"
        }

        case class SomeRoleC() {
          def method(): String = "C"
        }

        val roleA = SomeRoleA()
        val roleB = SomeRoleB()
        val roleC = SomeRoleC()
        And("some play relationships")
        someCore play roleA
        someCore play roleB
        someCore play roleC

        When("dispatching without sorting")
        val r1: String = +someCore method()
        Then("the sorting should do nothing and keep the roles sorted as specified through their binding sequence")
        r1 shouldBe "C"

        When("dispatching with sorting")
        implicit var dd = DispatchQuery.empty.sortedWith(reverse)
        val r2: String = +someCore method()
        Then("the sorting should reorder them")
        r2 shouldBe "A"

        When("dispatching with type based sorting")
        dd = DispatchQuery.empty.sortedWith {
          case (_: SomeRoleB, _: SomeRoleC) => swap
        }
        val r3: String = +someCore method()
        Then("the sorting should reorder them")
        r3 shouldBe "B"

        When("dispatching with filtering and type based sorting")
        dd = Bypassing(_.isInstanceOf[SomeRoleA]).sortedWith {
          case (_: SomeRoleB, _: SomeRoleC) => swap
        }
        val r4: String = +someCore method()
        Then("the sorting should reorder them")
        r4 shouldBe "B"
      }
    }

    scenario("Adding roles with cyclic calls and sorting them") {
      Given("some player and roles in a compartment")
      class SomeCore {
        def method(): String = "Core"
      }

      val someCore = new SomeCore()
      new SomeCompartment() {

        case class SomeRoleA() {
          def method(): String = {
            implicit val dd = Bypassing(_.isInstanceOf[SomeRoleA])
            +this method()
          }
        }

        case class SomeRoleB() {
          def method(): String = {
            implicit val dd = DispatchQuery.empty.sortedWith(reverse)
            +this method()
          }
        }

        val roleA = SomeRoleA()
        val roleB = SomeRoleB()
        And("some play relationships")
        someCore play roleA
        someCore play roleB

        When("dispatching")
        val r1: String = +someCore method()
        Then("the sorting should prevent cyclic dispatching")
        And("return the result of the execution of the core method")
        r1 shouldBe "Core"
      }
    }
  }
}