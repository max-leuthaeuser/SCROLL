package scroll.tests.parameterized

import scroll.internal.support.impl.QueryStrategies.MatchAny
import scroll.internal.support.impl.QueryStrategies.WithProperty
import scroll.internal.support.impl.QueryStrategies.WithResult
import scroll.tests.mocks._

class QueryStrategiesTest extends AbstractParameterizedSCROLLTest {

  test("MatchAny") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val alsoExpected = new RoleA()
        alsoExpected.valueC = "no"
        new CoreA play expected play alsoExpected
        val actual: Seq[RoleA] = roleQueries.all[RoleA](MatchAny())
        actual shouldBe Seq(expected, alsoExpected)
      }
    }
  }

  test("WithProperty") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"
        new CoreA play expected play notExpected
        val actual: Seq[RoleA] = roleQueries.all[RoleA](WithProperty("valueC", "valueC"))
        actual should contain only expected
      }
    }
  }

  test("WithResult") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"
        new CoreA play expected play notExpected
        val actual: Seq[RoleA] = roleQueries.all[RoleA](WithResult("valueC", "valueC"))
        actual should contain only expected
      }
    }
  }

  test("one with custom matcher") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"
        new CoreA play role1 play role2
        val matcher = (r: RoleA) => r match {
          case r: RoleA => r.valueC == "yes"
          case null => false
        }
        val actual: RoleA = roleQueries.one[RoleA](matcher)
        actual shouldBe role2
      }
    }
  }

  test("one with MatchAny") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"
        new CoreA play role1 play role2
        val actual: RoleA = roleQueries.one[RoleA]()
        actual shouldBe role1
      }
    }
  }

}
