package scroll.tests.parameterized

import scroll.tests.mocks.CompartmentUnderTest
import scroll.tests.mocks.CoreA

class RoleGroupsTest extends AbstractParameterizedSCROLLTest {

  test("Validating role group cardinality") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val acc1 = new CoreA()
      val acc2 = new CoreA()
      new CompartmentUnderTest(c, cc) {

        class Source

        class Target

        val source = new Source
        val target = new Target
        val roleGroupName = "Transaction"
        val transaction = roleGroups.create(roleGroupName).containing[Source, Target](1, 1)(2, 2)
        roleGroups.checked {
          acc1 play source
          acc2 play target
        }
        the[RuntimeException] thrownBy {
          roleGroups.checked {
            acc2 drop target
          }
        } should have message s"Constraint set for inner cardinality of role group '$roleGroupName' violated!"
        the[RuntimeException] thrownBy {
          roleGroups.checked {
            acc1 play target
          }
        } should have message s"Constraint set for inner cardinality of role group '$roleGroupName' violated!"
      } shouldNot be(null)
    }
  }
}
