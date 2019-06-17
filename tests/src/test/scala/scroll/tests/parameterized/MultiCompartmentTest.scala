package scroll.tests.parameterized

import scroll.internal.errors.SCROLLErrors._
import scroll.tests.mocks._

class MultiCompartmentTest extends AbstractParameterizedSCROLLTest {

  test("Specifying a MultiCompartment") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val p = new CoreB()
      new MultiCompartmentUnderTest(c, cc) {
        (+p).i() match {
          case Right(_) => fail("There should be no Right here")
          case Left(f@IllegalRoleInvocationDispatch(_, _, _)) => fail(f.toString)
          case Left(RoleNotFound(c, _, _)) => c shouldBe p.toString
        }
        val rA = new RoleA()
        p play rA
        (+rA).i() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(1))
          case Left(error) => fail(error.toString)
        }
        val rB = new RoleB()
        rA play rB
        var actual: Seq[Int] = (+p).i()
        actual.size shouldBe 2
        var expected = Seq(2, 1)
        actual shouldBe expected
        var actualLists: Seq[Seq[Int]] = (+p).is()
        actual.size shouldBe 2
        var expectedLists = Seq(Seq(2, 2), Seq(1, 1))
        actualLists shouldBe expectedLists
        val rC = new RoleC()
        rB play rC
        actual = (+p).i()
        actual.size shouldBe 3
        expected = Seq(3, 2, 1)
        actual shouldBe expected
        actualLists = (+p).is()
        actual.size shouldBe 3
        expectedLists = Seq(Seq(3, 3), Seq(2, 2), Seq(1, 1))
        actualLists shouldBe expectedLists
      } shouldNot be(null)
    }
  }

}
