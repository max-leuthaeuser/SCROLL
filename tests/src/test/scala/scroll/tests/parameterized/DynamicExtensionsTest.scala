package scroll.tests.parameterized

import scroll.tests.mocks._

class DynamicExtensionsTest extends AbstractParameterizedSCROLLTest {

  test("Removing dynamic extensions and invoking methods") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCore <+> someRole
        someCore <+> new RoleB()
        someCore <-> someRole
        val exptected = someCore.a()
        val actual: Int = (+someCore).a()
        exptected shouldBe actual
        (+someCore).hasExtension[RoleA] shouldBe false
        (+someCore).hasExtension[RoleB] shouldBe true
        val resB: String = (+someCore).b()
        resB shouldBe "b"
      } shouldNot be(null)
    }
  }

}
