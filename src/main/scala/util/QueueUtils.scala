package util

import scala.collection.immutable.Queue

object QueueUtils
{
  def swap[T](
    l: Queue[T],
    a: T,
    b: T
    ): Queue[T] = l.map {
    case `a` => b
    case `b` => a
    case e => e
  }

  def swapWithOrder[T](
    l: Queue[T],
    t: (T, T)
    ): Queue[T] =
  {
    val (a, b) = (t._1, t._2)

    l.map {
      case `a` => return l
      case `b` => return swap(l, a, b)
      case e => e
    }
  }

  def copy[T](q: Queue[T]): Queue[T] = q.map(identity)

  def remove[T](
    elem: T,
    q: Queue[T]
    ): Queue[T] = q.diff(Seq(elem))
}
