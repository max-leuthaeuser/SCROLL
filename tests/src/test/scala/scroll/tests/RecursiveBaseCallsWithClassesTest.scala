package scroll.tests

import org.scalatest._
import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery._

class RecursiveBaseCallsWithClassesTest extends FeatureSpec with GivenWhenThen with Matchers {

  class CoreType {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole extends Compartment {

    class RoleTypeA {
      implicit val dd = Bypassing((o: AnyRef) => {
        o == this || !o.isInstanceOf[CoreType]
      })

      def someMethod(): Unit = {
        println(s"RoleTypeA($this)::someMethod()")
        (+this).someMethod()
      }
    }

    class RoleTypeB {
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
        val c = new CoreType()
        val r = new RoleTypeA()
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
        val c1 = new CoreType()
        val c2 = new CoreType()
        val rA1 = new RoleTypeA()
        val rA2 = new RoleTypeA()
        val rB = new RoleTypeB()
        val player1 = c1 play rA1
        rA1 play rB
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