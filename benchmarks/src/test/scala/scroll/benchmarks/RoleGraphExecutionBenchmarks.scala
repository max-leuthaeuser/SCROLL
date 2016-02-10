package scroll.benchmarks

import org.scalameter.api._
import SCROLLBenchmarkConfig._

trait RoleGraphExecutionBenchmarks extends BenchmarkHelper {

  private def runPlayBenchmark() = using(input) config(
    exec.benchRuns -> NUM_OF_RUNS,
    exec.independentSamples -> NUM_OF_VMS
    ) in {
    case (players, roles) =>
      val comp = new MockCompartment()
      (0 until players).foreach(playerID => {
        val player = new MockPlayer(playerID)
        (0 until roles).foreach(roleID => {
          comp.addPlaysRelation(player, new MockRole(roleID))
        })
      })
  }

  private def runInvokeRoleBenchmark() = using(compartments) config(
    exec.benchRuns -> NUM_OF_RUNS,
    exec.independentSamples -> NUM_OF_VMS
    ) in {
    c => c.invokeAtRole()
  }

  private def runInvokeDirectlyBenchmark() = using(compartments) config(
    exec.benchRuns -> NUM_OF_RUNS,
    exec.independentSamples -> NUM_OF_VMS
    ) in {
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

    measure method "play (kiama)" in {
      backend = KIAMA
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

    measure method "invoke role method (kiama)" in {
      backend = KIAMA
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

    measure method "call role method directly (kiama)" in {
      backend = KIAMA
      runInvokeDirectlyBenchmark()
    }
  }
}