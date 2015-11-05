package scroll.internal.util

object Many {
  def * = Many()
}

case class Many() extends Ordered[Int] {
  override def compare(that: Int): Int = 1 // Many is larger as any Int

  override def toString: String = "*"
}
