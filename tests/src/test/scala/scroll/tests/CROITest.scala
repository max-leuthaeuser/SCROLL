package scroll.tests

import mocks._
import scroll.internal.formal.CROI

class CROITest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for CROIs.")

  feature("Specifying a CROI manually") {
    scenario("CROI is filled manually") {
      val p = getClass.getResource("/Bank.crom").getPath

      new CompartmentUnderTest() with CROI {
        Given("A natural, a role and a compartment instance")
        val n = new CoreA()
        val r = new RoleA()

        When("A specific valid CROM instance is given")
        Then("it should be wellformed")
        wellformed(p) shouldBe true
        And("specifying a CROI")

        addNatural(n)
        addRole(r)
        addCompartment(this)
        addPlays(n, this, r)
      }
    }

  }
}
