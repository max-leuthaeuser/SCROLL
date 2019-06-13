package scroll.tests.parameterized

import java.io.IOException

import scroll.tests.mocks.CompartmentUnderTest

class ThrowableInRoleMethodsTest extends AbstractParameterizedSCROLLTest {

  class CoreType

  class ExceptionShowcase(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {

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

  test("Handling thrown Error") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new ExceptionShowcase(c, cc) {
        val core = new CoreType()
        core play new Exceptional()
        an[Error] should be thrownBy (+core).roleMethodWithError()
      } shouldNot be(null)
    }
  }

  test("Handling thrown unchecked Exception") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new ExceptionShowcase(c, cc) {
        val core = new CoreType()
        core play new Exceptional()
        an[RuntimeException] should be thrownBy (+core).roleMethodWithUncheckedException()
      } shouldNot be(null)
    }
  }

  test("Handling thrown checked Exception") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new ExceptionShowcase(c, cc) {
        val core = new CoreType()
        core play new Exceptional()
        an[IOException] should be thrownBy (+core).roleMethodWithCheckedException()
      } shouldNot be(null)
    }
  }

}
