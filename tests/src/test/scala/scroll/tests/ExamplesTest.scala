package scroll.tests

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.examples._

class ExamplesTest extends FeatureSpec with GivenWhenThen with Matchers {
  // do not want info or debug logging at all here
  System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error")

  info("Test spec for scroll.examples.")

  Feature("Running scroll.examples") {
    Scenario("University") {
      When("Running the University example")
      Then("There should be no error or exception.")
      UniversityExample.main(null)
    }

    Scenario("Bank") {
      When("Running the Bank example")
      Then("There should be no error or exception.")
      BankExample.main(null)
    }

    Scenario("API Calls") {
      When("Running the APICalls example")
      Then("There should be no error or exception.")
      APICallsExample.main(null)
    }

    Scenario("Robot") {
      When("Running the Robot example")
      Then("There should be no error or exception.")
      RobotExample.main(null)
    }

    Scenario("Expression Problem") {
      When("Running the Expression Problem example")
      Then("There should be no error or exception.")
      ExpressionProblemExample.main(null)
    }
  }
}
