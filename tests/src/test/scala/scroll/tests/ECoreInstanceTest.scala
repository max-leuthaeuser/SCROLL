package scroll.tests

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.formal.CROM
import scroll.tests.mocks.SomeCompartment

class ECoreInstanceTest extends FeatureSpec with GivenWhenThen with Matchers with SCROLLTestConfig {
  info("Test spec for loading ECore CROM model instances.")

  feature("Loading an ECore CROM model instance") {
    scenario("No model is loaded") {
      new SomeCompartment(cached) with CROM {
        When("No model is available")
        Then("it can not be wellformed")
        wellformed shouldBe false
      }
    }

    scenario("Loading from a valid path containing a valid model") {
      val p = getClass.getResource("/Bank.crom").getPath

      new SomeCompartment(cached) with CROM {
        When("A specific valid CROM instance is given")
        withModel(p)
        Then("it should be wellformed")
        wellformed shouldBe true
      }
    }
  }
}
