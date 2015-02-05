package internal.util

import scala.collection.immutable.Queue

/**
 * Generic Queue ordering utility.
 */
object QueueUtils {
  /**
   * Swap to Elements of type T ind the given Queue l.
   *
   * @param l The queue to swap the elements in it.
   * @param a Element a that gets swapped with b.
   * @param b Element b that gets swapped with a.
   * @return a new Queue containing all elements of l but element a and b swapped.
   */
  def swap[T](
    l: Queue[T],
    a: T,
    b: T): Queue[T] = l.map {
    case `a` => b
    case `b` => a
    case e => e
  }

  /**
   * The same as swap but only swaps of element a and b are occurring
   * in that exact order like given with parameter t.
   *
   * @param l The queue to swap the elements in it.
   * @param t A tuple containing element a (as ._1) and b (as ._2) in the necessary order.
   * @return a new Queue containing all elements of l but element a and b swapped if they were found in the order given with t.
   */
  def swapWithOrder[T](
    l: Queue[T],
    t: (T, T)): Queue[T] =
    {
      val (a, b) = (t._1, t._2)

      l.map {
        case `a` => return l
        case `b` => return swap(l, a, b)
        case e => e
      }
    }

  /**
   * Simply copies a Queue.
   *
   * @param q the Queue to copy.
   * @return a new Queue containing all elements of q.
   */
  def copy[T](q: Queue[T]): Queue[T] = q.map(identity)

  /**
   * Removes an element of type T from the given Queue.
   *
   * @param elem the element to remove
   * @param q the queue to remove the element from.
   * @return a new Queue with all elements of q except elem.
   */
  def remove[T](
    elem: T,
    q: Queue[T]): Queue[T] = q.diff(Seq(elem))
}
