package internal

// removes warnings by Eclipse about using implicit conversion
import scala.language.implicitConversions

trait Context extends Compartment {

  private var cLocalBounds: List[Any] = initList
  protected def initList: List[Any] = List[Any]()

  def Bind(binds: => Unit) = new {
    def For(body: => Unit): Unit = {
      binds
      body
      cLocalBounds.foreach(plays.remove)
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