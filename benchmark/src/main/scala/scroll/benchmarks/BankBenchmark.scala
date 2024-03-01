package scroll.benchmarks

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State

object BankBenchmark {

  @State(Scope.Benchmark)
  class Shared {
    val bankExample: BankExample = new BankExample()
  }

  @State(Scope.Thread)
  class Local extends BankBenchmarkConfig.Exhaustive {

    var bankExample: BankExample = scala.compiletime.uninitialized

    @Setup
    def setup(shared: Shared): Unit = synchronized {
      bankExample = shared.bankExample.build(players, roles, transactions, cached)
    }

  }

}

class BankBenchmark extends AbstractBenchmark with BankBenchmarkConfig.Exhaustive {

  import BankBenchmark._

  @Benchmark
  def buildTimes: BankExample = new BankExample().build(players, roles, transactions, cached)

  @Benchmark
  def runTimes(local: Local): Boolean = local.bankExample.bank.executeTransactions()

}
