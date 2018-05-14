package scroll.tests

import org.scalatest._
import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery._

class RecursiveBaseCallsTest extends FeatureSpec with GivenWhenThen with Matchers {

  class CoreType {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole extends Compartment {

    case class RoleTypeA(id: String = "RoleTypeA") {
      implicit val dd = Bypassing(_.equals(this))

      def someMethod(): Unit = {
        println(s"RoleTypeA($this)::someMethod()")
        (+this).someMethod()
      }
    }

    case class RoleTypeB(id: String = "RoleTypeB") {
      implicit val dd = Bypassing((o: AnyRef) => {
        o.equals(this) || !o.isInstanceOf[CoreType]
      })

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
        val p = new CoreType
        val r = RoleTypeA()
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
        val p = new CoreType
        val rA = RoleTypeA()
        val rB = RoleTypeB()
        val player = p play rA play rB
        val output = new java.io.ByteArrayOutputStream()
        When("calling base")
        Console.withOut(output) {
          player.someMethod()
        }
        val actual = streamToSeq(output)
        val expected = Seq(
          s"RoleTypeA($rA)::someMethod()",
          s"RoleTypeB($rB)::someMethod()",
          s"CoreType($p)::someMethod()"
        )
        Then("the calls should be in the correct order")
        actual should contain theSameElementsInOrderAs expected
      }
    }
  }
}