package scroll.tests

import scroll.tests.mocks._

class QueryStrategiesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info(s"Test spec for QueryStrategies with cache = '$cached'.")

  Feature("Querying objects using different query strategies") {
    Scenario("Testing MatchAny") {
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

    Scenario("Testing WithProperty") {
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

    Scenario("Testing WithResult") {
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

    Scenario("Testing one with custom matcher") {
      Given("A compartment")
      new CompartmentUnderTest() {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"

        new CoreA play role1 play role2

        When("using this query strategy")
        val matcher = (r: RoleA) => r match {
          case r: RoleA => r.valueC == "yes"
          case _ => false
        }
        val actual: RoleA = one[RoleA](matcher)
        Then("the result should be correct")
        actual shouldBe role2
      }
    }

    Scenario("Testing one with MatchAny") {
      Given("A compartment")
      new CompartmentUnderTest() {
        val role1 = new RoleA()
        val role2 = new RoleA()
        role2.valueC = "yes"

        new CoreA play role1 play role2

        When("using this query strategy")
        val actual: RoleA = one[RoleA]()
        Then("the result should be correct")
        actual shouldBe role1
      }
    }

  }

}
