package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery._
import scroll.tests.mocks.CompartmentUnderTest
import scroll.tests.mocks.CoreA

class RoleSortingTest extends AbstractParameterizedSCROLLTest {

  test("Adding roles and sorting them") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {

        case class SomeRoleA() {
          def method(): String = "A"
        }

        case class SomeRoleB() {
          def method(): String = "B"
        }

        case class SomeRoleC() {
          def method(): String = "C"
        }

        val roleA = SomeRoleA()
        val roleB = SomeRoleB()
        val roleC = SomeRoleC()
        someCore play roleA
        someCore play roleB
        someCore play roleC

        {
          given DispatchQuery = DispatchQuery()
          val r1: String = (+someCore).method()
          r1 shouldBe "C"
        }

        {
          given DispatchQuery = DispatchQuery().sortedWith(reverse)
          val r2: String = (+someCore).method()
          r2 shouldBe "A"
        }

        {
          given DispatchQuery = DispatchQuery().sortedWith {
            case (_: SomeRoleB, _: SomeRoleC) => swap
          }
          val r3: String = (+someCore).method()
          r3 shouldBe "B"
        }

        {
          given DispatchQuery = Bypassing(_.isInstanceOf[SomeRoleA]).sortedWith {
            case (_: SomeRoleB, _: SomeRoleC) => swap
          }
          val r4: String = (+someCore).method()
          r4 shouldBe "B"
        }

      }
    }
  }

  class SomeCore {
    def method(): String = "Core"
  }

  test("Adding roles with cyclic calls and sorting them") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new SomeCore()
      new CompartmentUnderTest(c, cc) {

        case class SomeRoleA() {
          def method(): String = {
            given DispatchQuery = Bypassing(_.isInstanceOf[SomeRoleA])
            (+this).method()
          }
        }

        case class SomeRoleB() {
          def method(): String = {
            given DispatchQuery = DispatchQuery().sortedWith(reverse)
            (+this).method()
          }
        }

        val roleA = SomeRoleA()
        val roleB = SomeRoleB()
        someCore play roleA
        someCore play roleB
        val r1: String = (+someCore).method()
        r1 shouldBe "Core"
      }
    }
  }

}
