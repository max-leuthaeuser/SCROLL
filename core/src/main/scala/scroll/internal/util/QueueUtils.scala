package scroll.internal.util

/**
  * Generic Queue ordering utility.
  */
object QueueUtils {
  /**
    * Checks if the given Queue 'in' of type T contains a chain (non-empty queue)
    * starting with an element matching constraint 'from' and ending with one matching 'to'.
    *
    * @param from the starting constraint
    * @param to the end constraint
    * @param in the queue to check
    * @return true if the given Seq 'in' of type T contains a chain (non-empty seq)
    *         starting with an element matching constraint 'from' and ending with one matching 'to', false otherwise.
    */
  def hasPath[T](from: T => Boolean, to: T => Boolean, in: Seq[T]): Boolean = {
    val s = in.indexWhere(from)
    val e = in.lastIndexWhere(to)
    s != -1 && e != -1 && s < e
  }
}
