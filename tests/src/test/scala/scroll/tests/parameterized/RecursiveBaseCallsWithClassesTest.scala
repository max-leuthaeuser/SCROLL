package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery._
import scroll.tests.mocks.CompartmentUnderTest

class RecursiveBaseCallsWithClassesTest extends AbstractParameterizedSCROLLTest {

  class CoreType {

    def someMethod(): Unit = println(s"CoreType($this)::someMethod()")
  }

  class MultiRole(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {

    class RoleTypeA {

      given DispatchQuery =
        Bypassing { (o: AnyRef) =>
          o == this || !o.isInstanceOf[CoreType]
        }

      def someMethod(): Unit = {
        println(s"RoleTypeA($this)::someMethod()")
        (+this).someMethod()
      }

    }

    class RoleTypeB {
      given DispatchQuery = Bypassing(_ == this)

      def someMethod(): Unit = {
        println(s"RoleTypeB($this)::someMethod()")
        (+this).someMethod()
      }

    }

  }

  test("Adding roles and doing a normal base call") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiRole(c, cc) {
        val c      = new CoreType()
        val r      = new RoleTypeA()
        val player = c play r
        val output = new java.io.ByteArrayOutputStream()
        Console.withOut(output) {
          player.someMethod()
        }
        val actual   = streamToSeq(output)
        val expected = Seq(s"RoleTypeA($r)::someMethod()", s"CoreType($c)::someMethod()")
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }

  test("Adding roles and chaining base calls recursively") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiRole(c, cc) {
        val c1      = new CoreType()
        val c2      = new CoreType()
        val rA1     = new RoleTypeA()
        val rA2     = new RoleTypeA()
        val rB      = new RoleTypeB()
        val player1 = c1 play rA1
        rA1 play rB
        val player2 = c2 play rA2
        val output  = new java.io.ByteArrayOutputStream()
        Console.withOut(output) {
          player1.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeB($rB)::someMethod()",
          s"RoleTypeA($rA1)::someMethod()",
          s"CoreType($c1)::someMethod()"
        )
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }

}
