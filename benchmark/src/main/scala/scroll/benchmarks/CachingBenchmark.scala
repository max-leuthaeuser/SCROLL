package scroll.benchmarks

import org.openjdk.jmh.annotations.Benchmark

import org.openjdk.jmh.annotations._

object CachingBenchmark {

  @State(Scope.Benchmark)
  class Shared {
    var cachingExample: CachingExample = new CachingExample()
  }


  @State(Scope.Thread)
  class Local {

    var cachingExample: CachingExample = _

    @Param(Array("100", "1000", "10000"))
    var roles: Int = _

    @Setup
    def setup(shared: Shared): Unit = synchronized {
      cachingExample = shared.cachingExample.build(roles)
    }
  }

}

class CachingBenchmark extends AbstractBenchmark {

  import CachingBenchmark._

  private def reps(reps: Int, c: CachingExample#SomeCompartment): Int = {
    var result = 0
    var index = 0
    while (index < reps) {
      result = c.run()
      index += 1
    }
    result
  }

  @Benchmark
  def measureCached10(local: Local): Int = reps(10, local.cachingExample.cachedCompartment)

  @Benchmark
  def measureCached100(local: Local): Int = reps(100, local.cachingExample.cachedCompartment)

  @Benchmark
  def measureCached1000(local: Local): Int = reps(1000, local.cachingExample.cachedCompartment)

  @Benchmark
  def measureNonCached10(local: Local): Int = reps(10, local.cachingExample.noncachedCompartment)

  @Benchmark
  def measureNonCached100(local: Local): Int = reps(100, local.cachingExample.noncachedCompartment)

  @Benchmark
  def measureNonCached1000(local: Local): Int = reps(1000, local.cachingExample.noncachedCompartment)


}
