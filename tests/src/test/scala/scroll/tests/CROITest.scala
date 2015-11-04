package scroll.tests

import mocks.{CoreA, SomeCompartment}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.formal.CROI

class CROITest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for CROIs.")

  feature("Specifying a CROI manually") {
    scenario("CROI is filled manually") {
      val p = getClass.getResource("/Bank.crom").getPath

      new SomeCompartment with CROI {
        Given("A natural, a role and a compartment instance")
        val n = new CoreA()
        val r = new RoleA()

        When("A specific valid CROM instance is given")
        withModel(p)
        Then("it should be wellformed")
        wellformed shouldBe true
        And("specifying a CROI")

        addNatural(n)
        addRole(r)
        addCompartment(this)
        addPlays(n, this, r)
        println(croi)
      }
    }

  }
}
