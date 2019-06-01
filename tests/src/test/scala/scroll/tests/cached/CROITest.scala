package scroll.tests.cached

import scroll.internal.formal.CROI
import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks._

class CROITest extends AbstractSCROLLTest {
  info(s"Test spec for CROIs with cache = '$cached'.")

  Feature("Specifying a CROI manually") {
    Scenario("CROI is filled manually") {
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
