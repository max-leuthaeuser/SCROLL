import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import scroll.internal.Compartment
import scroll.internal.ecore.CROMInstance

class ECoreInstanceTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for loading ECore CROM model instances.")

  feature("Loading an ECore CROM model instance") {
    scenario("From path") {
      new Compartment with CROMInstance {
        path = "src/test/scala/mocks/Bank.crom"

        When("A specific valid CROM instance is given")
        val model = construct()
        Then("Axiom1 should hold")
        model.axiom1 shouldBe true
        And("Axiom2 should hold")
        model.axiom2 shouldBe true
        And("Axiom3 should hold")
        model.axiom3 shouldBe true
        And("Axiom4 should hold")
        model.axiom4 shouldBe true
        And("Axiom5 should hold")
        model.axiom5 shouldBe true
        And("hence model should be wellformed")
        model.wellformed shouldBe true
      }
    }
  }
}
