package scroll.tests

import scroll.internal.errors.SCROLLErrors.IllegalRoleInvocationDispatch
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import mocks._

class MultiCompartmentTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for the return types in multi compartments.")

  feature("Return in MultiCompartments specification and typing") {
    scenario("Specifying a MultiCompartment") {
      Given("A multi compartment, a player and attached roles")

      val p = new CoreB
      new MultiCompartmentUnderTest() {
        When("Calling a non-existing base method only")
        Then("The expected error should occur")
        +p i() match {
          case Right(_) => fail("There should be no Right here")
          case Left(f@IllegalRoleInvocationDispatch(_, _, _)) => fail(f.toString)
          case Left(RoleNotFound(c, _, _)) => c shouldBe p.toString
        }

        val rA = new RoleA

        When("Specifying a role-playing relationship")
        p play rA

        And("Calling a single role method")
        Then("The called method should return the correct value")
        +rA i() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(1))
          case Left(error) => fail(error.toString)
        }

        val rB = new RoleB
        When("specifying a role that plays another role")
        rA play rB

        Then("All the called method should return the correct values")
        var actual: Seq[Int] = +p i()
        actual.size shouldBe 2
        var expected = Seq(2, 1)
        actual shouldBe expected

        var actualLists: Seq[Seq[Int]] = +p is()
        actual.size shouldBe 2
        var expectedLists = Seq(Seq(2, 2), Seq(1, 1))
        actualLists shouldBe expectedLists

        val rC = new RoleC
        When("Adding another role")
        rB play rC

        Then("All the called methods should return the correct values")
        actual = +p i()
        actual.size shouldBe 3
        expected = Seq(3, 2, 1)
        actual shouldBe expected

        actualLists = +p is()
        actual.size shouldBe 3
        expectedLists = Seq(Seq(3, 3), Seq(2, 2), Seq(1, 1))
        actualLists shouldBe expectedLists
      }
    }
  }
}
