import org.scalameter.api._

object Benchmarks extends Bench.Group {
  performance of "memory" config (
    reports.resultDir -> "target/benchmarks/memory"
    ) in {
    include(new RoleGraphMemoryBenchmarks {})
  }

  performance of "running time" config (
    reports.resultDir -> "target/benchmarks/time"
    ) in {
    include(new RoleGraphExecutionBenchmarks {})
  }
}
