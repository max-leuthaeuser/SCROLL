package scroll.tests

import scroll.examples._

class ExamplesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for scroll.examples.")

  feature("Running scroll.examples") {
    scenario("University") {
      When("Running the University example")
      Then("There should be no error or exception.")
      UniversityExample.main(null)
    }

    scenario("Bank") {
      When("Running the Bank example")
      Then("There should be no error or exception.")
      BankExample.main(null)
    }

    scenario("API Calls") {
      When("Running the APICalls example")
      Then("There should be no error or exception.")
      APICallsExample.main(null)
    }

    scenario("Robot") {
      When("Running the Robot example")
      Then("There should be no error or exception.")
      RobotExample.main(null)
    }

    scenario("Expression Problem") {
      When("Running the Expression Problem example")
      Then("There should be no error or exception.")
      ExpressionProblemExample.main(null)
    }
  }
}
