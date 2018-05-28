package scroll.tests

import java.io.IOException

import org.junit.Assert.fail
import org.junit.Test
import scroll.internal.Compartment

class ThrowableInRoleMethodsTest {

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

  @Test
  def testErrorInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithError()
        fail("Should throw an Error")
      } catch {
        case _: Error => // all good
      }
    }
  }

  @Test
  def testUncheckedExceptionInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithUncheckedException()
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testCheckedExceptionInRoleMethod(): Unit = {
    new ExceptionShowcase() {
      val core = new CoreType()
      core play new Exceptional()
      try {
        (+core).roleMethodWithCheckedException()
        fail("Should throw an IOException")
      } catch {
        case _: IOException => // all good
      }
    }
  }

}