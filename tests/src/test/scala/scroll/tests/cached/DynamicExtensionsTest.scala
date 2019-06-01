package scroll.tests.cached

import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks._

class DynamicExtensionsTest extends AbstractSCROLLTest {
  info(s"Test spec for an excerpt of the dynamic extension concept with cache = '$cached'.")

  Feature("Adding dynamic extensions") {
    Scenario("Removing dynamic extsions and invoking methods") {
      Given("some player and a dynamic extension in a compartment")
      val someCore = new CoreA()
      new CompartmentUnderTest() {
        val someRole = new RoleA()
        And("adding a dynamic extension")
        someCore <+> someRole
        someCore <+> new RoleB()

        When("dropping the dynamic extension")
        someCore <-> someRole

        Then("the call must be invoked on the core object")
        someCore.a()
        (+someCore).a()

        And("a dynamic extension should be dropped correctly")
        (+someCore).hasExtension[RoleA] shouldBe false
        And("binding to RoleB is left untouched of course")
        (+someCore).hasExtension[RoleB] shouldBe true

        And("method invocation should work.")
        val resB: String = (+someCore).b()
        resB shouldBe "b"
      }
    }
  }
}
