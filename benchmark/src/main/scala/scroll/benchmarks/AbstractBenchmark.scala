package scroll.benchmarks

import org.openjdk.jmh.annotations._

import java.util.concurrent.TimeUnit

/** Shared JMH configuration for all SCROLL benchmarks in this subproject.
  *
  * Concrete benchmarks inherit the same mode, warmup, measurement, fork, and state settings so their results are easier
  * to compare across different scenarios.
  */
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
abstract class AbstractBenchmark
