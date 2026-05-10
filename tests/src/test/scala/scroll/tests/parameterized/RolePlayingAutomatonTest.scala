package scroll.tests.parameterized

import org.scalatest.concurrent.Waiters._
import org.scalatest.time.SpanSugar._
import scroll.internal.rpa.RolePlayingAutomaton
import scroll.internal.rpa.RolePlayingAutomaton._
import scroll.tests.mocks._

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class RolePlayingAutomatonTest extends AbstractParameterizedSCROLLTest {

  class ACompartment(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {
    val player = new CoreA()
    val roleA  = new RoleA()
    val roleB  = new RoleB()
    val roleC  = new RoleC()

    val w = new Waiter()

    class MyRPA extends RolePlayingAutomaton {

      private case object StateA extends RPAState

      private case object StateB extends RPAState

      private case object StateC extends RPAState

      when(Start) { case Event(BindRole, _) => goto(StateA) }

      when(StateA) { case Event(BindRole, _) => goto(StateB) }

      when(StateB) { case Event(BindRole, _) => goto(StateC) }

      when(StateC) { case Event(Terminate, _) => w.dismiss(); goto(Stop) }

      onTransition {
        case Start -> StateA  => player play roleA; self ! BindRole
        case StateA -> StateB => player play roleB; self ! BindRole
        case StateB -> StateC => player play roleC; self ! Terminate
      }

      run()
    }

    (Use[MyRPA] For this) ! BindRole
  }

  test("Binding roles") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new ACompartment(c, cc) {
        w.await(timeout(10 seconds))
        (+player).isPlaying[RoleA] shouldBe true
        (+player).isPlaying[RoleB] shouldBe true
        (+player).isPlaying[RoleC] shouldBe true
      }
    }
  }

  test("Concurrent run calls only start one processing loop") {
    class CountingRPA extends RolePlayingAutomaton {

      private case object Running extends RPAState

      private val runningTransitionCount = new AtomicInteger(0)
      private val runningSignal          = new CountDownLatch(1)

      when(Start) { case Event(BindRole, _) =>
        Thread.sleep(50)
        goto(Running)
      }

      when(Running) { case Event(BindRole, _) => goto(Running) }

      onTransition { case Start -> Running =>
        runningTransitionCount.incrementAndGet()
        runningSignal.countDown()
      }

      def send(data: RPAData): Unit = self ! data

      def awaitRunning(): Boolean = runningSignal.await(5, TimeUnit.SECONDS)

      def transitionCount: Int = runningTransitionCount.get()
    }

    val rpa = new CountingRPA()
    Await.result(Future.sequence((1 to 8).map(_ => Future(rpa.run()))), 10.seconds)

    (1 to 8).foreach(_ => rpa.send(BindRole))

    rpa.awaitRunning() shouldBe true
    Thread.sleep(200)
    rpa.transitionCount shouldBe 1
    rpa.halt()
  }

}
