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

  @Benchmark
  def measureCached(local: Local): Int = local.cachingExample.cachedCompartment.run()

  @Benchmark
  def measureNonCached(local: Local): Int = local.cachingExample.noncachedCompartment.run()

}
