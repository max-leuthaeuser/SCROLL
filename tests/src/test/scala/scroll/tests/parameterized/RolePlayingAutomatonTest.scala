package scroll.tests.parameterized

import org.scalatest.concurrent.Waiters._
import org.scalatest.time.SpanSugar._
import scroll.internal.rpa.RolePlayingAutomaton
import scroll.internal.rpa.RolePlayingAutomaton._
import scroll.tests.mocks._

class RolePlayingAutomatonTest extends AbstractParameterizedSCROLLTest {

  private val player = new CoreA()
  private val w = new Waiter

  class ACompartment(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {
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

  test("Binding roles") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      new ACompartment(c, cc) {
        w.await(timeout(10 seconds))
        (+player).isPlaying[RoleA] shouldBe true
        (+player).isPlaying[RoleB] shouldBe true
        (+player).isPlaying[RoleC] shouldBe true
      } shouldNot be(null)
    }
  }

}
