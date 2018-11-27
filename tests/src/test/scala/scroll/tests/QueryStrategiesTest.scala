package scroll.tests

import scroll.tests.mocks._

class QueryStrategiesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for QueryStrategies.")

  feature("Querying objects using different query strategies") {
    scenario("Testing MatchAny") {
      Given("A compartment and a MatchAny Strategy")
      new CompartmentUnderTest() {
        val expected = new RoleA()
        val alsoExpected = new RoleA()
        alsoExpected.valueC = "no"

        new CoreA play expected play alsoExpected

        When("using this query strategy")
        val actual: Seq[RoleA] = all[RoleA](MatchAny())
        Then("the result should be correct")
        actual shouldBe Seq(expected, alsoExpected)
      }
    }

    scenario("Testing WithProperty") {
      Given("A compartment and a WithProperty Strategy")
      new CompartmentUnderTest() {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"

        new CoreA play expected play notExpected

        When("using this query strategy")
        val actual: Seq[RoleA] = all[RoleA](WithProperty("valueC", "valueC"))
        Then("the result should be correct")
        actual should contain only expected
      }
    }

    scenario("Testing WithResult") {
      Given("A compartment and a WithResult Strategy")
      new CompartmentUnderTest() {
        val expected = new RoleA()
        val notExpected = new RoleA()
        notExpected.valueC = "no"

        new CoreA play expected play notExpected

        When("using this query strategy")
        val actual: Seq[RoleA] = all[RoleA](WithResult("valueC", "valueC"))
        Then("the result should be correct")
        actual should contain only expected
      }
    }

  }

}
