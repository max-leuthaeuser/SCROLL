package scroll.tests

import java.io.IOException

import org.scalatest._
import scroll.internal.Compartment

class ThrowableInRoleMethodsTest extends FeatureSpec with GivenWhenThen with Matchers {

  info("Test spec for recursive base calls.")

  class CoreType

  class ExceptionShowcase extends Compartment {

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

  feature("Handling throwable in role methods") {
    scenario("Handling thrown error") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the error should be thrown")
        an[Error] should be thrownBy (+core).roleMethodWithError()
      }
    }

    scenario("Handling thrown unchecked exception") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the unchecked exception should be thrown")
        an[RuntimeException] should be thrownBy (+core).roleMethodWithUncheckedException()
      }
    }

    scenario("Handling thrown checked exception") {
      Given("a player and a role in a compartment")
      new ExceptionShowcase() {
        val core = new CoreType()
        core play new Exceptional()
        When("calling the role method")
        Then("the checked exception should be thrown")
        an[IOException] should be thrownBy (+core).roleMethodWithCheckedException()
      }
    }
  }

}