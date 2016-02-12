package scroll.benchmarks

object BenchmarkHelper {

  sealed trait Backend

  case class JGRAPHT() extends Backend

  case class KIAMA() extends Backend

  case class CACHED() extends Backend

}

trait BenchmarkHelper {
  // generators
  protected val roles = List(100)
  protected val players = List(10)
  protected val invokes = 1000

  implicit class RichElapsed(f: => Unit) {

    def elapsed(): Double = {
      val start = System.nanoTime()
      f
      val end = System.nanoTime()

      (end - start) / 1e3
    }

  }

  implicit class RichElapsedT[T](f: => T) {

    def elapsed(): (T, Double) = {
      val start = System.nanoTime()
      val result = f
      val end = System.nanoTime()

      (result, (end - start) / 1e3)
    }

  }

}
