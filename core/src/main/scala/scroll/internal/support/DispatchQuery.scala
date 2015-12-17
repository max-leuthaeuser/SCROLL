package scroll.internal.support

import scroll.internal.util.{QueueUtils, ReflectiveHelper}

/**
  * Companion object for [[scroll.internal.support.DispatchQuery]] providing
  * some static dispatch functions and a fluent dispatch query creation API.
  */
object DispatchQuery extends ReflectiveHelper {
  /**
    * Function always returning true
    */
  val anything: Any => Boolean = _ => true
  /**
    * Function always returning false
    */
  val nothing: Any => Boolean = _ => false

  def From(f: Any => Boolean) = new {
    def To(t: Any => Boolean) = new {
      def Through(th: Any => Boolean) = new {
        def Bypassing(b: Any => Boolean): DispatchQuery = new DispatchQuery(f, t, th, b)
      }
    }
  }

  def empty: DispatchQuery = new DispatchQuery(empty = true)
}

/**
  * All provided query function must be side-effect free!
  *
  * @param from query function selecting the starting element for the role dispatch query
  * @param to query function selecting the end element for the role dispatch query
  * @param through query function specifying intermediate elements for the role dispatch query
  * @param bypassing query function specifying all elements to be left out for the role dispatch query
  */
class DispatchQuery(
                     val from: Any => Boolean = DispatchQuery.anything,
                     val to: Any => Boolean = DispatchQuery.anything,
                     val through: Any => Boolean = DispatchQuery.anything,
                     val bypassing: Any => Boolean = DispatchQuery.nothing,
                     private val empty: Boolean = false) {
  def isEmpty: Boolean = empty

  def reorder(anys: Seq[Any]): Seq[Any] = {
    def apply(in: Seq[Any]): Seq[Any] = {
      if (isEmpty) return in
      // we only apply the reordering on the path from DispatchQuery.from to DispatchQuery.to
      QueueUtils.hasPath(from, to, in) match {
        case true =>
          lazy val startIndex = in.indexWhere(from)
          lazy val endIndex = in.lastIndexWhere(to)

          if (startIndex == 0 || endIndex == 1) {
            return in.filter(through).filterNot(bypassing)
          }

          val head = in.take(startIndex - 1)
          val path = in.slice(startIndex, endIndex - 1)
          val tail = in.slice(endIndex, in.size)

          head ++ path.filter(through).filterNot(bypassing) ++ tail
        case false => in
      }
    }
    require(null != anys)
    apply(anys.distinct).reverse
  }
}