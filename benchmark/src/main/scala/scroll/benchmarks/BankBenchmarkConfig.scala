package scroll.benchmarks

import org.openjdk.jmh.annotations.Param

object BankBenchmarkConfig {

  trait Fast {

    @Param(Array("true"))
    var cached: Boolean = scala.compiletime.uninitialized

    @Param(Array("100"))
    var transactions: Int = scala.compiletime.uninitialized

    @Param(Array("100"))
    var roles: Int = scala.compiletime.uninitialized

    @Param(Array("100"))
    var players: Int = scala.compiletime.uninitialized

  }

  trait Exhaustive {

    @Param(Array("true", "false"))
    var cached: Boolean = scala.compiletime.uninitialized

    @Param(Array("10", "100", "1000"))
    var transactions: Int = scala.compiletime.uninitialized

    @Param(Array("10", "100", "1000"))
    var roles: Int = scala.compiletime.uninitialized

    @Param(Array("10", "100", "1000"))
    var players: Int = scala.compiletime.uninitialized

  }

}
