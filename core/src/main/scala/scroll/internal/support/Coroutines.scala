package scroll.internal.support

import scroll.internal.Compartment

import scala.collection.mutable
import scala.util.continuations._

/**
  * Allowes to use coroutines in your compartment.
  */
trait Coroutines {
  self: Compartment =>

  private lazy val scheduler = new Scheduler

  sealed trait Trampoline

  case object Done extends Trampoline

  case class Continue(next: Unit => Trampoline) extends Trampoline

  class Scheduler {
    lazy val ready = new mutable.Queue[Unit => Trampoline]

    def yld {
      if (ready.nonEmpty) {
        val thunk = ready.dequeue()
        thunk() match {
          case Continue(next) => ready.enqueue(next)
          case _ =>
        }
      }
    }

    def addThunk(thunk: Unit => Trampoline) {
      ready.enqueue(thunk)
    }
  }

  def coroutine(body: => Unit@cps[Trampoline]): Unit =
    scheduler.addThunk((Unit) => reset {
      body
      Done
    })

  def yld: Unit@cps[Trampoline] = {
    shift { (cont: Unit => Trampoline) => Continue(cont) }
  }

  def yieldAll: Unit = {
    scheduler.yld
  }
}