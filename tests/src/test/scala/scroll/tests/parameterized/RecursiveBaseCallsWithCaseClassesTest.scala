package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery._
import scroll.tests.mocks.CompartmentUnderTest

class RecursiveBaseCallsWithCaseClassesTest extends AbstractParameterizedSCROLLTest {

  case class CoreType(id: String) {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {

    case class RoleTypeA(id: String) {
      implicit val dd = Bypassing((o: AnyRef) => {
        o == this || !o.isInstanceOf[CoreType]
      })

      def someMethod(): Unit = {
        println(s"RoleTypeA($this)::someMethod()")
        (+this).someMethod()
      }
    }

    case class RoleTypeB(id: String) {
      implicit val dd = Bypassing(_ == this)

      def someMethod(): Unit = {
        println(s"RoleTypeB($this)::someMethod()")
        (+this).someMethod()
      }
    }

  }

  test("Adding roles and doing a normal base call") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiRole(c, cc) {
        val c = CoreType("p")
        val r = RoleTypeA("r")
        val player = c play r
        val output = new java.io.ByteArrayOutputStream()
        Console.withOut(output) {
          player.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeA($r)::someMethod()",
          s"CoreType($c)::someMethod()"
        )
        actual should contain theSameElementsInOrderAs expected
      } shouldNot be(null)
    }
  }

  test("Adding roles and chaining base calls recursively") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiRole(c, cc) {
        val c1 = CoreType("c1")
        val c2 = CoreType("c2")
        val rA1 = RoleTypeA("rA1")
        val rA2 = RoleTypeA("rA2")
        val rB = RoleTypeB("rB")
        val player1 = c1 play rA1 play rB
        val player2 = c2 play rA2
        val output = new java.io.ByteArrayOutputStream()
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
      } shouldNot be(null)
    }
  }

}
