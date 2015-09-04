import mocks.CoreA
import mocks.SomeCompartment
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import scroll.internal.formal.CROIInstance

class CROIInstanceTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for CROI instances.")

  feature("Specifying a CROI instance manually") {
    scenario("CROI instance is filled manually") {
      new SomeCompartment with CROIInstance {
        Given("A natural, a role and a compartment instance")
        val n = new CoreA()
        val r = new RoleA()

        When("A specific valid CROM instance is given")
        withModel("src/test/scala/mocks/Bank.crom")
        Then("it should be wellformed")
        wellformed shouldBe true
        And("specifying a CROI instance")

        addNatural(n)
        addRole(r)
        addCompartment(this)
        addPlays(n, this, r)
        println(croi)
      }
    }

  }
}
