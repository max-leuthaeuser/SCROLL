package internal

// removes warnings by Eclipse about using implicit conversion
import scala.language.implicitConversions
import annotations.Role

trait ContextInternals extends Compartment {
  implicit def AnyToWrappedWith(c: Any) = new WrappedWith(c)

  case class WrappedWith[T](private val c: T) {
    private var curr_r: Any = _

    private def isRole(value: Any): Boolean = value.getClass.isAnnotationPresent(classOf[Role])

    def With(r: Any): WrappedWith[T] =
      {
        require(isRole(r), "Argument for 'With' must be a role (you maybe want to add the @Role annotation).")
        curr_r = r
        c play r
        this
      }

    def From(from: Any): WrappedWith[T] =
      {
        require(isRole(from), "Argument for 'From' must be a role (you maybe want to add the @Role annotation).")
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