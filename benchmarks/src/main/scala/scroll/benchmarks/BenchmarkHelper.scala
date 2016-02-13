package scroll.benchmarks

object BenchmarkHelper {

  sealed trait Backend

  object JGRAPHT extends Backend

  object KIAMA extends Backend

  object CACHED extends Backend

}

trait BenchmarkHelper {
  // generators
  protected val roles = List(1,10,100)
  protected val players = List(1,10,100)
  protected val invokes = 100

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
