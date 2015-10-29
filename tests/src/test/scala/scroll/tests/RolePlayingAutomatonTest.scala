package scroll.tests

import mocks.{CoreA, SomeCompartment}
import org.scalatest.concurrent.AsyncAssertions._
import org.scalatest.time.SpanSugar._
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.rpa.RolePlayingAutomaton
import scroll.internal.rpa.RolePlayingAutomaton.{BindRole, RPAState, Start, Stop, Terminate, Use}

class RolePlayingAutomatonTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role playing automaton.")

  feature("Specifying a role playing automaton") {
    scenario("Binding roles") {
      val w = new Waiter
      Given("A natural, some role instances")
      val player = new CoreA()
      When("A role playing automaton is specified")
      class ACompartment extends SomeCompartment {
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()

        class MyRPA extends RolePlayingAutomaton {

          private case object StateA extends RPAState

          private case object StateB extends RPAState

          private case object StateC extends RPAState

          when(Start) {
            case Event(BindRole, _) => goto(StateA)
          }

          when(StateA) {
            case Event(BindRole, _) => goto(StateB)
          }

          when(StateB) {
            case Event(BindRole, _) => goto(StateC)
          }

          when(StateC) {
            case Event(Terminate, _) => w.dismiss(); goto(Stop)
          }

          onTransition {
            case Start -> StateA => player play roleA; self ! BindRole
            case StateA -> StateB => player play roleB; self ! BindRole
            case StateB -> StateC => player play roleC; self ! Terminate
          }

          run()
        }

        (Use[MyRPA] For this) ! BindRole
      }

      new ACompartment() {
        w.await(timeout(1 second))
        Then("player should play RoleA")
        (+player).isPlaying[RoleA] shouldBe true
        And("player should play RoleB")
        (+player).isPlaying[RoleB] shouldBe true
        And("player should play RoleC")
        (+player).isPlaying[RoleC] shouldBe true
      }
    }
  }
}
