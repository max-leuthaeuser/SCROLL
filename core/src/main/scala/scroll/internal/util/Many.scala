package scroll.internal.util

object Many {
  def * = new Many()
}

class Many(private val self: Int = 0) extends AnyVal with Ordered[Int] {
  override def compare(that: Int): Int = 0 // Many is as large as any Int
}
