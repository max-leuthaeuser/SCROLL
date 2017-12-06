package scroll.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param

class BuildTimes extends AbstractBenchmark {
  @Param(Array("10", "100", "1000"))
  var transactions: Int = _

  @Param(Array("10", "100", "1000"))
  var roles: Int = _

  @Param(Array("10", "100", "1000"))
  var players: Int = _

  @Benchmark
  def measureBuildTimes: BankExample = new BankExample().build(players, roles, transactions)

}
