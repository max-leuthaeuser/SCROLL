package scroll.tests

import org.scalatest._
import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery._

class RecursiveBaseCallsWithCaseClassesTest extends FeatureSpec with GivenWhenThen with Matchers {

  case class CoreType(id: String) {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole extends Compartment {

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

  private def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = "\n"): Seq[String] =
    in.toString.split(splitAt).toSeq

  info("Test spec for recursive base calls.")

  feature("Dispatching of base calls") {
    scenario("Adding roles and doing a normal base call") {
      Given("a player and a role in a compartment")
      new MultiRole() {
        val p = CoreType("p")
        val r = RoleTypeA("r")
        val player = p play r
        val output = new java.io.ByteArrayOutputStream()
        When("calling base")
        Console.withOut(output) {
          player.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeA($r)::someMethod()",
          s"CoreType($p)::someMethod()"
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
        val p1 = CoreType("p1")
        val p2 = CoreType("p2") play RoleTypeA("r2")
        val rA = RoleTypeA("rA")
        val rB = RoleTypeB("rB")
        val player = p1 play rA play rB
        val output = new java.io.ByteArrayOutputStream()
        When("calling base")
        Console.withOut(output) {
          player.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeB($rB)::someMethod()",
          s"RoleTypeA($rA)::someMethod()",
          s"CoreType($p1)::someMethod()"
        )
        Then("the calls should be in the correct order")
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }
}