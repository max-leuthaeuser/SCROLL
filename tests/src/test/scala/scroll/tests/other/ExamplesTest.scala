package scroll.tests.other

import scroll.examples._
import scroll.tests.AbstractSCROLLTest

class ExamplesTest extends AbstractSCROLLTest {
  info("Test spec for scroll.examples.")

  Feature("Running scroll.examples") {
    Scenario("University") {
      When("Running the University example")
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        UniversityExample.main(null)
      }
      val actual = streamToSeq(output)
      Then("There should be no error or exception and the printed output should be correct.")
      actual should contain theSameElementsInOrderAs Seq(
        "I am a person",
        "Player equals core: true",
        "I am a student",
        "Right(hans)",
        "Role core equals core: true",
        "I am a professor",
        "Core equals core playing a role: true",
        "Teaching: hans"
      )
    }

    Scenario("Bank") {
      When("Running the Bank example")
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        BankExample.main(null)
      }
      val actual = streamToSeq(output)
      Then("There should be no error or exception and the printed output should be correct.")

      val expected = Seq(
        """### Before transaction ###""",
        """Balance for Stan:""",
        """CheckingsAccount: scroll.examples.BankExample\$Bank\$CheckingsAccount@.{6,8} -> Right\(10.00 USD\)""",
        """Balance for Brian:""",
        """SavingsAccount: scroll.examples.BankExample\$Bank\$SavingsAccount@.{6,8} -> Right\(0.00 USD\)""",
        """Executing from Role.""",
        """Executing from Player.""",
        """Increasing with fee.""",
        """Transferred '10.00 USD' from 'scroll.examples.BankExample\$Transaction\$Source@.{6,8}' to 'scroll.examples.BankExample\$Transaction\$Target@.{6,8}'.""",
        """### After transaction ###""",
        """Balance for Stan:""",
        """CheckingsAccount: scroll.examples.BankExample\$Bank\$CheckingsAccount@.{6,8} -> Right\(0.00 USD\)""",
        """Balance for Brian:""",
        """SavingsAccount: scroll.examples.BankExample\$Bank\$SavingsAccount@.{6,8} -> Right\(9.00 USD\)"""
      )

      actual.lazyZip(expected).foreach((a, e) => a should fullyMatch regex e.r)
    }


    Scenario("API Calls") {
      When("Running the APICalls example")
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        APICallsExample.main(null)
      }

      val actual = streamToSeq(output)
      Then("There should be no error or exception and the printed output should be correct.")
      actual should contain theSameElementsInOrderAs Seq(
        "Call A is correct.",
        "Call B is fixed now. :-)",
        "Call C is correct."
      )
    }

    Scenario("Robot") {
      When("Running the Robot example")
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        RobotExample.main(null)
      }
      val actual = streamToSeq(output)
      Then("There should be no error or exception and the printed output should be correct.")
      actual should contain theSameElementsInOrderAs Seq("I am Pete and moving to the kitchen with my wheels w.r.t. sensor value of 100.")
    }

    Scenario("Expression Problem") {
      When("Running the Expression Problem example")
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        ExpressionProblemExample.main(null)
      }
      val actual = streamToSeq(output)
      Then("There should be no error or exception and the printed output should be correct.")
      actual should contain theSameElementsInOrderAs Seq(
        "Eval: Right(9)",
        "Show: Right(Right(-Right(2)) + Right(11))"
      )
    }
  }
}
