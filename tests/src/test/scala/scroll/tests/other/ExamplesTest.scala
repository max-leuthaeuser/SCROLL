package scroll.tests.other

import scroll.examples._
import scroll.tests.AbstractSCROLLTest

class ExamplesTest extends AbstractSCROLLTest {

  test("scroll.examples.UniversityExample") {
    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      UniversityExample.main(null)
    }
    val actual = streamToSeq(output)
    actual should contain theSameElementsInOrderAs Seq(
      "I am a person",
      "Player equals core: true",
      "I am a student",
      "hans",
      "Role core equals core: true",
      "I am a professor",
      "Core equals core playing a role: true",
      "Teaching: hans"
    )
  }

  test("scroll.examples.BankExample") {
    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      BankExample.main(null)
    }
    val actual = streamToSeq(output)

    val expected = Seq(
      """### Before transaction ###""",
      """Balance for Stan:""",
      """CheckingsAccount 'scroll.examples.BankExample\$Bank\$CheckingsAccount@.{6,8}': 10.0""",
      """Balance for Brian:""",
      """SavingsAccount 'scroll.examples.BankExample\$Bank\$SavingsAccount@.{6,8}': 0.0""",
      """Executing from Role.""",
      """Executing from Player.""",
      """Increasing with fee.""",
      """Transferred '10.0' from 'scroll.examples.BankExample\$Transaction\$Source@.{6,8}' to 'scroll.examples.BankExample\$Transaction\$Target@.{6,8}'.""",
      """### After transaction ###""",
      """Balance for Stan:""",
      """CheckingsAccount 'scroll.examples.BankExample\$Bank\$CheckingsAccount@.{6,8}': 0.0""",
      """Balance for Brian:""",
      """SavingsAccount 'scroll.examples.BankExample\$Bank\$SavingsAccount@.{6,8}': 9.0"""
    )

    actual.lazyZip(expected).foreach((a, e) => a should fullyMatch regex e.r)
  }


  test("scroll.examples.APICallsExample") {
    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      APICallsExample.main(null)
    }

    val actual = streamToSeq(output)
    actual should contain theSameElementsInOrderAs Seq(
      "Call A is correct.",
      "Call B is fixed now. :-)",
      "Call C is correct."
    )
  }

  test("scroll.examples.RobotExample") {
    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      RobotExample.main(null)
    }
    val actual = streamToSeq(output)
    actual should contain theSameElementsInOrderAs Seq("I am Pete and moving to the kitchen with my wheels w.r.t. sensor value of 100.")
  }

  test("Expression Problem") {
    val output = new java.io.ByteArrayOutputStream()
    Console.withOut(output) {
      ExpressionProblemExample.main(null)
    }
    val actual = streamToSeq(output)
    actual should contain theSameElementsInOrderAs Seq(
      "Eval: 9",
      "Show: -2 + 11"
    )
  }

}
