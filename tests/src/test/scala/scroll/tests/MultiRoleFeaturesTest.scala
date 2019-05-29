package scroll.tests

import scroll.internal.MultiCompartment
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._
import mocks.CoreA

class MultiRoleFeaturesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info(s"Test spec for an excerpt of the role concept for multi roles with cache = '$cached'.")

  Feature("Role playing") {
    Scenario("Playing roles and invoking all methods") {
      Given("some player and roles in a compartment")
      val someCore = new CoreA()

      case class RoleA(id: String = "RoleA")

      case class RoleB(id: String = "RoleB")

      case class RoleC(id: String = "RoleC")

      new MultiCompartment() {
        implicit var dd = DispatchQuery.empty.sortedWith {
          case (_: RoleC, _: RoleA) => swap
          case (_: RoleB, _: RoleA) => swap
          case (_: RoleC, _: RoleB) => swap
        }
        val roleA = RoleA()
        val roleB = RoleB()
        val roleC = RoleC()
        And("some play relationships")
        someCore play roleA play roleB play roleC

        When("invoking methods")
        Then("the call must be invoked on all methods with the correct ordering")
        val expected = Seq(Right("RoleC"), Right("RoleB"), Right("RoleA"))
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected
          case Left(error) => fail(error.toString)
        }

        When("invoking methods with sorting (reverse ordering)")
        Then("the call must be invoked on all methods with the correct ordering")
        dd = DispatchQuery.empty.sortedWith {
          case (_: RoleA, _: RoleC) => swap
          case (_: RoleA, _: RoleB) => swap
          case (_: RoleB, _: RoleC) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected.reverse
          case Left(error) => fail(error.toString)
        }

        When("invoking methods with filtering")
        Then("the call must be invoked on all methods with the correct ordering")
        val expected2 = Seq(Right("RoleC"), Right("RoleB"))
        dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
          case (_: RoleC, _: RoleB) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected2
          case Left(error) => fail(error.toString)
        }

        When("invoking methods with filtering and sorting (reverse ordering)")
        Then("the call must be invoked on all methods with the correct ordering")
        dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
          case (_: RoleB, _: RoleC) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected2.reverse
          case Left(error) => fail(error.toString)
        }
      }
    }
  }
}
