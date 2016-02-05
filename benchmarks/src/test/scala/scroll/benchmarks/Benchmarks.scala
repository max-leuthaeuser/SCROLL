package scroll.benchmarks

import org.scalameter.api._

object Benchmarks extends Bench.Group {
  performance of "running time" config (
    reports.resultDir -> "target/benchmarks/time"
    ) in {
    include(new RoleGraphExecutionBenchmarks {})
  }
}
