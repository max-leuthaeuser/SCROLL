package internal

object DispatchQuery extends ReflectiveHelper {
  def From(f: Any => Boolean) = new {
    def To(t: Any => Boolean) = new {
      def Through(th: Any => Boolean) = new {
        def Bypassing(b: Any => Boolean): DispatchQuery = new DispatchQuery(f, t, th, b)
      }
    }
  }

  def empty: DispatchQuery = new DispatchQuery(empty = true)
}

class DispatchQuery(
  val from: Any => Boolean = _ => true,
  val to: Any => Boolean = _ => true,
  val through: Any => Boolean = _ => true,
  val bypassing: Any => Boolean = _ => false,
  private val empty: Boolean = false) {
  def isEmpty: Boolean = empty
}