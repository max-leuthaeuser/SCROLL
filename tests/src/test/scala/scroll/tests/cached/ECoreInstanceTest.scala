package scroll.tests.cached

import scroll.internal.formal.CROM
import scroll.tests.AbstractSCROLLTest

class ECoreInstanceTest extends AbstractSCROLLTest {
  info(s"Test spec for loading ECore CROM model instances with cache = '$cached'.")

  Feature("Loading an ECore CROM model instance") {
    Scenario("No model is loaded") {
      new CompartmentUnderTest() with CROM {
        When("No model is available")
        Then("it can not be wellformed")
        an[IllegalArgumentException] should be thrownBy wellformed("")
        an[IllegalArgumentException] should be thrownBy wellformed(null)
      }
    }

    Scenario("Loading from a valid path containing a valid model") {
      val p = getClass.getResource("/Bank.crom").getPath

      new CompartmentUnderTest() with CROM {
        When("A specific valid CROM instance is given")
        Then("it should be wellformed")
        wellformed(p) shouldBe true
      }
    }
  }
}
