package scroll.tests.parameterized

import scroll.tests.mocks._

class UnionTypesTest extends AbstractParameterizedSCROLLTest {

  test("Calling some role method directly") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRoleC = new RoleC()
        someCoreA play someRoleC
        val expectedI = 0
        val expectedS = 4
        val actualI: Int = someRoleC.unionTypedMethod(0)
        val actualS: Int = someRoleC.unionTypedMethod("four")
        expectedI shouldBe actualI
        expectedS shouldBe actualS
      } shouldNot be(null)
    }
  }

  test("Matching players and roles with Scala match") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA
        val matcher = new {
          def m[T: (CoreA or RoleA)#λ](param: T) = param match { // scalastyle:ignore
            case _: CoreA => -1
            case a: RoleA => a.a()
          }
        }
        someCore play roleA
        val expectedCore = -1
        val expectedRoleA = 0
        val actualCore = matcher.m(someCore)
        val actualRoleA = matcher.m(roleA)
        expectedCore shouldBe actualCore
        expectedRoleA shouldBe actualRoleA
      } shouldNot be(null)
    }
  }

}
