package scroll.benchmarks

import org.openjdk.jmh.annotations._
import scroll.internal.dispatch.SCROLLDynamic

import java.util.concurrent.TimeUnit
import scala.util.Random

object NoopBenchmark {

  @State(Scope.Thread)
  class Local {
    var x, y: Int             = scala.compiletime.uninitialized
    var player: SCROLLDynamic = scala.compiletime.uninitialized

    @Param(Array("true", "false"))
    var cached: Boolean = scala.compiletime.uninitialized

    @Setup(Level.Iteration)
    def setup(): Unit = {
      player = new NoopExample(cached).compartment.player
      x = Random.nextInt()
      y = Random.nextInt()
    }

  }

}

/** Measures role method dispatch overhead for a single role bound to a player, where each role method just forwards the
  * method call to its base.
  */
@OutputTimeUnit(TimeUnit.MICROSECONDS)
class NoopBenchmark extends AbstractBenchmark {

  import NoopBenchmark.Local

  @Benchmark
  def baseline(): Unit = {}

  @Benchmark
  def basecall_noargs(local: Local): Any =
    local.player.noArgs()

  @Benchmark
  def basecall_withargs(local: Local): Any =
    local.player.referenceArgAndReturn(local.player)

  @Benchmark
  def basecall_primitiveargs(local: Local): Any =
    local.player.primitiveArgsAndReturn(local.x, local.y)

}
