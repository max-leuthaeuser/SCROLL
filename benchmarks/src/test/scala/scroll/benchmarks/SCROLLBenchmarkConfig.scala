package scroll.benchmarks

object SCROLLBenchmarkConfig extends Enumeration {
  type Backend = Value
  val JGRAPHT, CACHED, KIAMA = Value
}

trait SCROLLBenchmarkConfig {

  import SCROLLBenchmarkConfig._

  protected var backend: Backend = JGRAPHT
}
