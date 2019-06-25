package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery._
import scroll.tests.mocks.CoreA
import scroll.tests.mocks.MultiCompartmentUnderTest

class MultiRoleFeaturesTest extends AbstractParameterizedSCROLLTest {

  case class RoleA(id: String = "RoleA")

  case class RoleB(id: String = "RoleB")

  case class RoleC(id: String = "RoleC")

  test("Playing roles and invoking all methods") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        implicit var dd = DispatchQuery.empty.sortedWith {
          case (_: RoleC, _: RoleA) => swap
          case (_: RoleB, _: RoleA) => swap
          case (_: RoleC, _: RoleB) => swap
        }
        val roleA = RoleA()
        val roleB = RoleB()
        val roleC = RoleC()
        someCore play roleA play roleB play roleC
        val expected = Seq(Right("RoleC"), Right("RoleB"), Right("RoleA"))
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected
          case Left(error) => fail(error.toString)
        }
        dd = DispatchQuery.empty.sortedWith {
          case (_: RoleA, _: RoleC) => swap
          case (_: RoleA, _: RoleB) => swap
          case (_: RoleB, _: RoleC) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected.reverse
          case Left(error) => fail(error.toString)
        }
        val expected2 = Seq(Right("RoleC"), Right("RoleB"))
        dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
          case (_: RoleC, _: RoleB) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected2
          case Left(error) => fail(error.toString)
        }
        dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
          case (_: RoleB, _: RoleC) => swap
        }
        (+someCore).id() match {
          case Right(actual) => actual shouldBe expected2.reverse
          case Left(error) => fail(error.toString)
        }
      } shouldNot be(null)
    }
  }

}
