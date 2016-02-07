package scroll.benchmarks

import org.scalameter.api._

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
      cached = false
      runPlayBenchmark()
    }

    measure method "play (cached)" in {
      cached = true
      runPlayBenchmark()
    }

    measure method "invoke role method" in {
      cached = false
      runInvokeRoleBenchmark()
    }

    measure method "invoke role method (cached)" in {
      cached = true
      runInvokeRoleBenchmark()
    }

    measure method "call role method directly" in {
      cached = false
      runInvokeDirectlyBenchmark()
    }

    measure method "call role method directly (cached)" in {
      cached = true
      runInvokeDirectlyBenchmark()
    }
  }
}