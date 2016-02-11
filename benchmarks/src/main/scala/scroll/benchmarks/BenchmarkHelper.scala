package scroll.benchmarks

object BenchmarkHelper {

  sealed trait Backend

  case class JGRAPHT() extends Backend

  case class KIAMA() extends Backend

  case class CACHED() extends Backend

}

trait BenchmarkHelper {
  // generators
  protected val roles = List(10, 1000)
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

}
