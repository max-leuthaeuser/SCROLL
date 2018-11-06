package scroll.tests

import scroll.internal.formal.CROM

class ECoreInstanceTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for loading ECore CROM model instances.")

  feature("Loading an ECore CROM model instance") {
    scenario("No model is loaded") {
      new CompartmentUnderTest() with CROM {
        When("No model is available")
        Then("it can not be wellformed")
        an[IllegalArgumentException] should be thrownBy wellformed("")
        an[IllegalArgumentException] should be thrownBy wellformed(null)
      }
    }

    scenario("Loading from a valid path containing a valid model") {
      val p = getClass.getResource("/Bank.crom").getPath

      new CompartmentUnderTest() with CROM {
        When("A specific valid CROM instance is given")
        Then("it should be wellformed")
        wellformed(p) shouldBe true
      }
    }
  }
}
