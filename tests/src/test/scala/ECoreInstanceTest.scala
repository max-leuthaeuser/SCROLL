import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.Compartment
import scroll.internal.formal.CROM

class ECoreInstanceTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for loading ECore CROM model instances.")

  feature("Loading an ECore CROM model instance") {
    scenario("No model is loaded") {
      new Compartment with CROM {
        When("No model is available")
        Then("it can not be wellformed")
        wellformed shouldBe false
      }
    }

    scenario("Loading from a valid path containing a valid model") {
      new Compartment with CROM {
        When("A specific valid CROM instance is given")
        withModel("tests/src/test/scala/mocks/Bank.crom")
        Then("it should be wellformed")
        wellformed shouldBe true
      }
    }
  }
}
