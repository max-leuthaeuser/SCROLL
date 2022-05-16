package scroll.internal.rpa

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.FSM
import akka.actor.LoggingFSM
import akka.actor.Props
import scroll.internal.compartment.impl.Compartment
import scroll.internal.rpa.RolePlayingAutomaton.RPAData
import scroll.internal.rpa.RolePlayingAutomaton.RPAState
import scroll.internal.rpa.RolePlayingAutomaton.Start
import scroll.internal.rpa.RolePlayingAutomaton.Stop
import scroll.internal.rpa.RolePlayingAutomaton.Uninitialized

import scala.reflect.ClassTag
import scala.reflect.classTag

/** Companion object for the [[scroll.internal.rpa.RolePlayingAutomaton]] containing predefined states and data objects
  * for messaging.
  */
object RolePlayingAutomaton {

  // some predefined states
  trait RPAState

  case object Start extends RPAState

  case object Stop extends RPAState

  // some predefined data objects for messaging
  trait RPAData

  case object Uninitialized extends RPAData

  case object BindRole extends RPAData

  case object RemoveRole extends RPAData

  case object TransferRole extends RPAData

  case object CheckConstraints extends RPAData

  case object Terminate extends RPAData

  protected class RPABuilder[T <: AnyRef: ClassTag]() {

    def For(comp: Compartment): ActorRef =
      ActorSystem().actorOf(Props(classTag[T].runtimeClass, comp), s"rpa_${comp.hashCode()}")

  }

  def Use[T <: AnyRef: ClassTag]: RPABuilder[T] = new RPABuilder[T]()
}

/** Use this trait to implement your own [[scroll.internal.compartment.impl.Compartment]] specific role playing
  * automaton. Please read the documentation for [[akka.actor.FSM]] carefully, since the features from that are
  * applicable for role playing automatons.
  *
  * Remember to call <code>run()</code> when you want to start this automaton in your
  * [[scroll.internal.compartment.impl.Compartment]] instance.
  *
  * This automaton will always start in state [[scroll.internal.rpa.RolePlayingAutomaton.Start]], so hook in there.
  *
  * Final state is always [[scroll.internal.rpa.RolePlayingAutomaton.Stop]], that will terminate the actor system for
  * this [[akka.actor.FSM]].
  *
  * Use the factory method <code>RolePlayingAutomaton.Use</code> to gain an instance of your specific FSM, e.g.:
  *
  * {{{
  * trait MyCompartment extends Compartment {
  * // ... some roles and interaction
  *
  * // your specific RPA here
  * class MyRolePlayingAutomaton extends RolePlayingAutomaton {
  * // specific behavior here
  * when(Start) {
  * // ...
  * }
  *
  * onTransition {
  * // ...
  * }
  *
  * run()
  * }
  *
  * Use[MyRolePlayingAutomaton] For this
  * }
  *
  * // start everything
  * new MyCompartment {}
  * }}}
  *
  * Some predefined event types for messaging are available in the companion object. You may want to define your own
  * states and event types. Simply use a companion object for this as well.
  */
trait RolePlayingAutomaton extends Actor with LoggingFSM[RPAState, RPAData] {

  /** Starts this automaton. Needs to be called first! Will set the initial state to
    * [[scroll.internal.rpa.RolePlayingAutomaton.Start]].
    */
  def run(): Unit = {
    log.debug(s"Starting RPA '${self.path}'")
    startWith(Start, Uninitialized)
    initialize()
  }

  /** Stops this automaton. Will set state to [[scroll.internal.rpa.RolePlayingAutomaton.Stop]] and terminates the actor
    * system for this [[akka.actor.FSM]].
    */
  def halt(): State = {
    val _ = context.system.terminate()
    stop()
  }

  when(Stop) {
    FSM.NullFunction
  }

  onTransition { case _ -> Stop =>
    log.debug(s"Stopping RPA '${self.path}'")
    val _ = halt()
  }

}
