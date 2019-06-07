package scroll.tests.parameterized

import scroll.tests.mocks.CompartmentUnderTest
import scroll.tests.mocks.CoreA

class RoleGroupsTest extends AbstractParameterizedSCROLLTest {

  test("Validating role group cardinality") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val acc1 = new CoreA()
      val acc2 = new CoreA()
      new CompartmentUnderTest(c, cc) {

        class Source

        class Target

        val source = new Source
        val target = new Target
        val transaction = RoleGroup("Transaction").containing[Source, Target](1, 1)(2, 2)
        RoleGroupsChecked {
          acc1 play source
          acc2 play target
        }
        a[RuntimeException] should be thrownBy {
          RoleGroupsChecked {
            acc2 drop target
          }
        }
        a[RuntimeException] should be thrownBy {
          RoleGroupsChecked {
            acc1 play target
          }
        }
      } shouldNot be(null)
    }
  }
}
