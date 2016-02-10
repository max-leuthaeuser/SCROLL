package scroll.benchmarks

import org.scalameter.api._
import SCROLLBenchmarkConfig._

trait RoleGraphExecutionBenchmarks extends BenchmarkHelper {
  val opts = Seq(
    exec.benchRuns -> NUM_OF_RUNS,
    exec.independentSamples -> NUM_OF_VMS
  )

  private def runPlayBenchmark() = using(input) config (opts: _*) in {
    case (players, roles) =>
      val comp = new MockCompartment()
      (0 until players).foreach(playerID => {
        val player = new MockPlayer(playerID)
        (0 until roles).foreach(roleID => {
          comp.addPlaysRelation(player, new MockRole(roleID))
        })
      })
  }

  private def runInvokeRoleBenchmark() = using(compartments) config (opts: _*) in {
    c => c.invokeAtRole()
  }

  private def runInvokeDirectlyBenchmark() = using(compartments) config (opts: _*) in {
    c => c.invokeDirectly()
  }


  performance of "RoleGraph" in {
    measure method "play" in {
      backend = JGRAPHT
      runPlayBenchmark()
    }

    measure method "play (cached)" in {
      backend = CACHED
      runPlayBenchmark()
    }

    measure method "invoke role method" in {
      backend = JGRAPHT
      runInvokeRoleBenchmark()
    }

    measure method "invoke role method (cached)" in {
      backend = CACHED
      runInvokeRoleBenchmark()
    }

    measure method "call role method directly" in {
      backend = JGRAPHT
      runInvokeDirectlyBenchmark()
    }


    measure method "call role method directly (cached)" in {
      backend = CACHED
      runInvokeDirectlyBenchmark()
    }
  }
}