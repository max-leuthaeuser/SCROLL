package internal

// removes warnings by Eclipse about using implicit conversion
import scala.language.implicitConversions

trait ContextInternals extends Compartment {
  implicit def AnyToWrappedWith(c: Any) = new WrappedWith(c)

  case class WrappedWith[T](private val c: T) {
    private var curr_r: Any = _

    def With(r: Any): WrappedWith[T] =
      {
        curr_r = r
        c play r
        this
      }

    def From(from: Any): WrappedWith[T] =
      {
        transferRole(from, c, curr_r)
        this
      }
  }

}

trait Context extends ContextInternals {
  def Bind(rel: WrappedWith[_]*)(body: => Unit) {
      body
      rel.foreach(plays.remove)
  }
}