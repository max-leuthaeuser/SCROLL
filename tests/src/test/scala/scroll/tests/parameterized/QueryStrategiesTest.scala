package scroll.tests.parameterized

import scroll.tests.mocks._

class QueryStrategiesTest extends AbstractParameterizedSCROLLTest {

  test("MatchAny") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val alsoExpected = new RoleA()
        alsoExpected.valueC = "no"
        new CoreA play expected play alsoExpected
        val actual: Seq[RoleA] = all[RoleA](MatchAny())
        actual shouldBe Seq(expected, alsoExpected)
      } shouldNot be(null)
    }
  }

  test("WithProperty") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"
        new CoreA play expected play notExpected
        val actual: Seq[RoleA] = all[RoleA](WithProperty("valueC", "valueC"))
        actual should contain only expected
      } shouldNot be(null)
    }
  }

  test("WithResult") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"
        new CoreA play expected play notExpected
        val actual: Seq[RoleA] = all[RoleA](WithResult("valueC", "valueC"))
        actual should contain only expected
      } shouldNot be(null)
    }
  }

  test("one with custom matcher") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"
        new CoreA play role1 play role2
        val matcher = (r: RoleA) => r match {
          case r: RoleA => r.valueC == "yes"
          case _ => false
        }
        val actual: RoleA = one[RoleA](matcher)
        actual shouldBe role2
      } shouldNot be(null)
    }
  }

  test("one with MatchAny") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"
        new CoreA play role1 play role2
        val actual: RoleA = one[RoleA]()
        actual shouldBe role1
      } shouldNot be(null)
    }
  }

}
