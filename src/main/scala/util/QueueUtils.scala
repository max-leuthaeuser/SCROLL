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
}
