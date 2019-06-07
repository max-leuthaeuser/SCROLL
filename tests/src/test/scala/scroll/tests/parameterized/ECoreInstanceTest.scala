package scroll.tests.parameterized

import scroll.internal.formal.CROM
import scroll.tests.mocks.CompartmentUnderTest

class ECoreInstanceTest extends AbstractParameterizedSCROLLTest {

  test("No model is loaded") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) with CROM {
        an[IllegalArgumentException] should be thrownBy wellformed("")
        an[IllegalArgumentException] should be thrownBy wellformed(null)
      } shouldNot be(null)
    }
  }

  test("Loading from a valid path containing a valid model") {
    val p = getClass.getResource("/Bank.crom").getPath
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) with CROM {
        wellformed(p) shouldBe true
      } shouldNot be(null)
    }
  }
}
