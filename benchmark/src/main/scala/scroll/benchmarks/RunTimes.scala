package scroll.benchmarks

import org.openjdk.jmh.annotations.Benchmark

import org.openjdk.jmh.annotations._

object RunTimes {

  @State(Scope.Benchmark)
  class Shared {
    var bank: BankExample = new BankExample()
  }


  @State(Scope.Thread)
  class Local {

    var bank: BankExample = _

    @Param(Array("10", "100", "1000"))
    var transactions: Int = _

    @Param(Array("10", "100", "1000"))
    var roles: Int = _

    @Param(Array("10", "100", "1000"))
    var players: Int = _

    @Setup
    def setup(shared: Shared): Unit = synchronized {
      bank = shared.bank.build(players, roles, transactions)
    }
  }

}

class RunTimes extends AbstractBenchmark {

  import RunTimes._

  @Benchmark
  def measureRunTimes(local: Local): Boolean = local.bank.benchmark()

}
