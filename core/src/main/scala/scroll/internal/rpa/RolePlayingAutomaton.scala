package scroll.internal.rpa

import scroll.internal.compartment.impl.Compartment
import scroll.internal.rpa.RolePlayingAutomaton._
import zio.{ Fiber, Queue, Runtime, Unsafe, ZIO }

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

  final case class Event(data: RPAData, stateData: RPAData)

  final case class Transition(state: RPAState, data: RPAData = Uninitialized)

  trait RPARef {
    def !(data: RPAData): Unit
  }

  protected class RPABuilder[T <: RolePlayingAutomaton: ClassTag]() {

    infix def For(comp: Compartment): RPARef = {
      val instance = instantiate[T](comp)
      instance.ref
    }

  }

  def Use[T <: RolePlayingAutomaton: ClassTag]: RPABuilder[T] = new RPABuilder[T]()

  private def instantiate[T <: RolePlayingAutomaton: ClassTag](comp: Compartment): T = {
    val clazz        = classTag[T].runtimeClass
    val constructors = clazz.getDeclaredConstructors.toList
    val withComp     = constructors.find { ctor =>
      val params = ctor.getParameterTypes
      params.length == 1 && params.head.isAssignableFrom(comp.getClass)
    }

    val ctor = withComp.orElse(constructors.find(_.getParameterCount == 0)).getOrElse {
      throw new IllegalArgumentException(
        s"Unable to instantiate ${clazz.getName} for compartment ${comp.getClass.getName}"
      )
    }

    ctor.setAccessible(true)
    if (ctor.getParameterCount == 1) {
      ctor.newInstance(comp).asInstanceOf[T]
    } else {
      ctor.newInstance().asInstanceOf[T]
    }
  }

}

/** Use this trait to implement your own [[scroll.internal.compartment.impl.Compartment]] specific role playing
  * automaton. This implementation uses ZIO to run a lightweight, single-threaded event loop.
  *
  * Remember to call <code>run()</code> when you want to start this automaton in your
  * [[scroll.internal.compartment.impl.Compartment]] instance.
  *
  * This automaton will always start in state [[scroll.internal.rpa.RolePlayingAutomaton.Start]], so hook in there.
  *
  * Final state is always [[scroll.internal.rpa.RolePlayingAutomaton.Stop]], which terminates the internal loop.
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
trait RolePlayingAutomaton {

  private val runtime   = Runtime.default
  private val stateLock = new AnyRef

  private val queue: Queue[RPAData] = Unsafe.unsafe { implicit u =>
    runtime.unsafe.run(Queue.unbounded[RPAData]).getOrThrow()
  }

  private var currentState: RPAState                                         = Start
  private var currentData: RPAData                                           = Uninitialized
  private var handlers: Map[RPAState, PartialFunction[Event, Transition]]    = Map.empty
  private var transitionHandler: PartialFunction[(RPAState, RPAState), Unit] = PartialFunction.empty
  private var fiber: Option[Fiber.Runtime[Nothing, Unit]]                    = None

  protected val self: RPARef = new RPARef {
    override def !(data: RPAData): Unit = enqueue(data)
  }

  private[rpa] def ref: RPARef = self

  /** Register event handler for a state. */
  def when(state: RPAState)(handler: PartialFunction[Event, Transition]): Unit =
    stateLock.synchronized {
      handlers = handlers.updated(state, handler)
    }

  /** Register transition handler. Multiple handlers can be chained. */
  def onTransition(handler: PartialFunction[(RPAState, RPAState), Unit]): Unit =
    stateLock.synchronized {
      transitionHandler = transitionHandler.orElse(handler)
    }

  def goto(state: RPAState, data: RPAData = Uninitialized): Transition = Transition(state, data)

  /** Starts this automaton. Needs to be called first! Will set the initial state to
    * [[scroll.internal.rpa.RolePlayingAutomaton.Start]].
    */
  def run(): Unit = start()

  /** Stops this automaton by interrupting the processing fiber. */
  def halt(): Unit = {
    val activeFiber = stateLock.synchronized {
      currentState = Stop
      val runningFiber = fiber
      fiber = None
      runningFiber
    }
    activeFiber.foreach { f =>
      Unsafe.unsafe { implicit u =>
        runtime.unsafe.run(f.interrupt).getOrThrow()
      }
    }
  }

  private def start(): Unit =
    stateLock.synchronized {
      if (fiber.isEmpty) {
        currentState = Start
        currentData = Uninitialized
        val loop         = processLoop
        val startedFiber = Unsafe.unsafe { implicit u =>
          runtime.unsafe.run(loop.forkDaemon).getOrThrow()
        }
        fiber = Some(startedFiber)
      }
    }

  private def enqueue(data: RPAData): Unit =
    Unsafe.unsafe { implicit u =>
      runtime.unsafe.run(queue.offer(data)).getOrThrow()
    }

  private def processLoop: ZIO[Any, Nothing, Unit] =
    queue.take.flatMap { message =>
      val snapshot = stateLock.synchronized {
        val state = currentState
        val data  = currentData
        handlers.get(state) match {
          case Some(handler) if handler.isDefinedAt(Event(message, data)) => Some((state, data, handler))
          case _                                                          => None
        }
      }

      snapshot match {
        case Some((state, data, handler)) =>
          val Transition(nextState, nextData)    = handler(Event(message, data))
          val (shouldContinue, transitionAction) = stateLock.synchronized {
            if (fiber.isEmpty || currentState == Stop) {
              (false, Option.empty[() => Unit])
            } else {
              currentState = nextState
              currentData = nextData
              val action = transitionHandler.lift(state -> nextState).map(handler => () => handler)
              (nextState != Stop, action)
            }
          }

          transitionAction.foreach(_())
          if (shouldContinue) processLoop else ZIO.unit
        case _ =>
          val shouldContinue = stateLock.synchronized {
            fiber.nonEmpty && currentState != Stop
          }
          if (shouldContinue) processLoop else ZIO.unit
      }
    }

}
