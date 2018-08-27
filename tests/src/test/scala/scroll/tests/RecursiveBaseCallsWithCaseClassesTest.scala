package scroll.tests

import scroll.internal.support.DispatchQuery._

class RecursiveBaseCallsWithCaseClassesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  case class CoreType(id: String) {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole extends CompartmentUnderTest {

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

  private def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

  info("Test spec for recursive base calls.")

  feature("Dispatching of base calls") {
    scenario("Adding roles and doing a normal base call") {
      Given("a player and a role in a compartment")
      new MultiRole() {
        val c = CoreType("p")
        val r = RoleTypeA("r")
        val player = c play r
        val output = new java.io.ByteArrayOutputStream()
        When("calling base")
        Console.withOut(output) {
          player.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeA($r)::someMethod()",
          s"CoreType($c)::someMethod()"
        )
        Then("the calls should be in the correct order")
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }

  feature("Dispatching of recursive base calls") {
    scenario("Adding roles and chaining base calls recursively") {
      Given("a player and two roles in a compartment")
      new MultiRole() {
        val c1 = CoreType("c1")
        val c2 = CoreType("c2")
        val rA1 = RoleTypeA("rA1")
        val rA2 = RoleTypeA("rA2")
        val rB = RoleTypeB("rB")
        val player1 = c1 play rA1 play rB
        val player2 = c2 play rA2
        val output = new java.io.ByteArrayOutputStream()
        When("calling base")
        Console.withOut(output) {
          player1.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeB($rB)::someMethod()",
          s"RoleTypeA($rA1)::someMethod()",
          s"CoreType($c1)::someMethod()"
        )
        Then("the calls should be in the correct order")
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }
}