import mocks.{SomeCompartment, CoreA}
import org.scalatest.{Matchers, GivenWhenThen, FeatureSpec}
import examples._

class ExamplesRoleSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for examples.")

  feature("Running examples") {
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

    scenario("Another Bank") {
      When("Running the AnotherBank example")
      Then("There should be no error or exception.")
      AnotherBankExample.main(null)
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

    scenario("Repmin Kiama Example") {
      When("Running the Repmin Kiama example")
      Then("There should be no error or exception.")
      RepminKiamaExample.main(null)
    }

    scenario("Math Kiama Example") {
      When("Running the Math Kiama example")
      Then("There should be no error or exception.")
      MathKiamaExample.main(null)
    }
  }
}
