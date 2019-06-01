package scroll.tests.cached

import java.io.IOException


import scroll.tests.AbstractSCROLLTest

class ThrowableInRoleMethodsTest extends AbstractSCROLLTest {
  info(s"Test spec for handling a Throwable in role methods with cache = '$cached'.")

  class CoreType

  class ExceptionShowcase extends CompartmentUnderTest {

    class Exceptional {
      def roleMethodWithError(): Unit = {
        throw new Error()
      }

      def roleMethodWithUncheckedException(): Unit = {
        throw new RuntimeException()
      }

      def roleMethodWithCheckedException(): Unit = {
        throw new IOException()
      }
    }

  }

  Feature("Handling Throwable in role methods") {
    Scenario("Handling thrown Error") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the Error should be thrown")
        an[Error] should be thrownBy (+core).roleMethodWithError()
      }
    }

    Scenario("Handling thrown unchecked Exception") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the unchecked Exception should be thrown")
        an[RuntimeException] should be thrownBy (+core).roleMethodWithUncheckedException()
      }
    }

    Scenario("Handling thrown checked Exception") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the checked Exception should be thrown")
        an[IOException] should be thrownBy (+core).roleMethodWithCheckedException()
      }
    }
  }

}
