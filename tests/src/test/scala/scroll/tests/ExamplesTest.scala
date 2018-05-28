package scroll.tests

import org.junit.Before
import org.junit.Test
import scroll.examples._

class ExamplesTest {

  @Before
  def initialize(): Unit = {
    // do not want info or debug logging at all here
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error")
  }

  @Test
  def testUniversityExample(): Unit = {
    UniversityExample.main(null)
  }

  @Test
  def testBankExample(): Unit = {
    BankExample.main(null)
  }

  @Test
  def testAPICallsExample(): Unit = {
    APICallsExample.main(null)
  }

  @Test
  def testRobotExample(): Unit = {
    RobotExample.main(null)
  }

  @Test
  def testExpressionProblemExample(): Unit = {
    ExpressionProblemExample.main(null)
  }

}
