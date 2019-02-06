package scroll.benchmarks

import org.openjdk.jmh.annotations.Param

object BankBenchmarkConfig {

  trait Fast {
    @Param(Array("true"))
    var cached: Boolean = _

    @Param(Array("100"))
    var transactions: Int = _

    @Param(Array("100"))
    var roles: Int = _

    @Param(Array("100"))
    var players: Int = _
  }

  trait Exhaustive {
    @Param(Array("true", "false"))
    var cached: Boolean = _

    @Param(Array("10", "100", "1000"))
    var transactions: Int = _

    @Param(Array("10", "100", "1000"))
    var roles: Int = _

    @Param(Array("10", "100", "1000"))
    var players: Int = _
  }

}