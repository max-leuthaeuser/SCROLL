package scroll.tests

import scroll.tests.mocks.CoreB

class MultiCompartmentTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for the return types in multi compartments.")

  feature("Return in MultiCompartments specification and typing") {
    scenario("Specifying a MultiCompartment") {
      Given("A multi compartment, a player and attached roles")

      val p = new CoreB
      new MultiCompartmentUnderTest() {
        val rA = new RoleA
        val rB = new RoleB
        val rC = new RoleC

        When("specifying a role that plays another role")
        p play rA
        rA play rB

        Then("The called method should return the correct values")
        var actual: Seq[Int] = +p i()
        actual.size shouldBe 2
        var expected = Seq(2, 1)
        actual shouldBe expected

        When("adding another role")
        rB play rC

        Then("The called method should return the correct values")
        actual = +p i()
        actual.size shouldBe 3
        expected = Seq(3, 2, 1)
        actual shouldBe expected
      }
    }
  }
}
