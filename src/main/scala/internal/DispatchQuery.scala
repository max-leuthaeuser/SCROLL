package internal

import internal.util.QueueUtils

import scala.collection.immutable.Queue

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

/**
 * All provided query function must be side-effect free!
 *
 * @param from query function selecting the starting element for the role dispatch query
 * @param to query function selecting the end element for the role dispatch query
 * @param through query function specifying intermediate elements for the role dispatch query
 * @param bypassing query function specifying all elements to be left out for the role dispatch query
 */
class DispatchQuery(
                     val from: Any => Boolean = _ => true,
                     val to: Any => Boolean = _ => true,
                     val through: Any => Boolean = _ => true,
                     val bypassing: Any => Boolean = _ => false,
                     private val empty: Boolean = false) {
  def isEmpty: Boolean = empty

  def reorder(anys: Queue[Any]): Queue[Any] = {
    require(null != anys)

    val dist_anys = anys.distinct

    if (isEmpty) return dist_anys.reverse

    // we only apply the reordering on the path from DispatchQuery.from to DispatchQuery.to
    QueueUtils.hasPath(from, to, dist_anys) match {
      case true =>
        val startIndex = dist_anys.indexWhere(from)
        val endIndex = dist_anys.indexWhere(to)

        if (startIndex == 0 || endIndex == 1) {
          return dist_anys.filter(through).filterNot(bypassing).reverse
        }

        val head = dist_anys.take(startIndex - 1)
        val path = dist_anys.slice(startIndex, endIndex - 1)
        val tail = dist_anys.slice(endIndex, dist_anys.size)

        (head ++ path.filter(through).filterNot(bypassing) ++ tail).reverse
      case false => dist_anys.reverse
    }
  }
}