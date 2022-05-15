package scroll.tests.parameterized

import scroll.internal.formal.CROI
import scroll.tests.mocks._

class CROITest extends AbstractParameterizedSCROLLTest {

  test("CROI is filled manually") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val p = getClass.getResource("/Bank.crom").getPath
      new CompartmentUnderTest(c, cc) with CROI {
        val n = new CoreA
        val r = new RoleA
        wellformed(p) shouldBe true
        addNatural(n)
        addRole(r)
        addCompartment(this)
        addPlays(n, this, r)
      }
    }
  }

}
