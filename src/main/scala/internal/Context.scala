package internal

// removes warnings by Eclipse about using implicit conversion
import scala.language.implicitConversions
import scala.concurrent.blocking
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import util.Log.info
import scala.concurrent.Await

trait Context extends Compartment {

  private var cLocalBounds: List[Any] = initList
  protected def initList: List[Any] = List[Any]()

  def Bind(binds: => Unit) = new {
    def Blocking(body: => Unit): Unit = {
      blocking {
        binds
        body
        cLocalBounds.foreach(plays.remove)
      }
    }

    def NonBlocking(body: => Unit): Unit = {
      val f = Future {
        binds
        body
        cLocalBounds.foreach(plays.remove)
      }

      f onComplete {
        case Success(s) => info("Success for non-blocking body.")
        case Failure(t) => throw new RuntimeException(t)
      }
    }
  }

  implicit class WrappedWith[T](val c: T) {
    private var curr_r: Any = _

    def With(r: Any): WrappedWith[T] =
      {
        curr_r = r
        c play r
        cLocalBounds = cLocalBounds :+ c
        this
      }

    def From(from: Any): WrappedWith[T] =
      {
        transferRole(from, c, curr_r)
        cLocalBounds = cLocalBounds diff List(from)
        this
      }
  }
}