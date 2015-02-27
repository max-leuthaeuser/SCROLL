import mocks.{ SomeCompartment, CoreA }
import org.scalatest.{ Matchers, GivenWhenThen, FeatureSpec }
import examples.BankExample
import examples.UniversityExample

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
  }
}
