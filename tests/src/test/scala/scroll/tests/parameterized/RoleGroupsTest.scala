package scroll.tests.parameterized

import scroll.internal.errors.SCROLLErrors.RoleGroupInnerCardinalityViolation
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

        val source        = new Source
        val target        = new Target
        val roleGroupName = "Transaction"
        val transaction   = roleGroups.create(roleGroupName).containing[Source, Target](1, 1)(2, 2)

        roleGroups.checked {
          acc1 play source
          acc2 play target
        }

        val innerViolation1 = the[RoleGroupInnerCardinalityViolation] thrownBy
          roleGroups.checked {
            acc2 drop target
          }
        innerViolation1.groupName shouldBe roleGroupName

        val innerViolation2 = the[RoleGroupInnerCardinalityViolation] thrownBy
          roleGroups.checked {
            acc1 play target
          }
        innerViolation2.groupName shouldBe roleGroupName

      }
    }
  }

  test("Validating nested role groups repeatedly") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val acc1 = new CoreA()
      val acc2 = new CoreA()
      new CompartmentUnderTest(c, cc) {

        class Source

        class Target

        val source = new Source
        val target = new Target
        val pair   = roleGroups.create("Pair").containing[Source, Target](1, 1)(2, 2)
        roleGroups.create("Transaction").containing(pair)(1, 1)(2, 2)

        roleGroups.checked {
          acc1 play source
          acc2 play target
        }

        roleGroups.checked {}
      }
    }
  }

}
