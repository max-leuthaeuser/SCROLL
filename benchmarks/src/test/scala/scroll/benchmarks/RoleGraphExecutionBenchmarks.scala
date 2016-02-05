package scroll.benchmarks

import org.scalameter.api._

trait RoleGraphExecutionBenchmarks extends BenchmarkHelper {
  performance of "RoleGraph" in {

    measure method "play" in {
      cached = false
      using(input) config(
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
    }

    measure method "play (cached)" in {
      cached = true
      using(input) config(
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
    }

    measure method "invoke role method" in {
      cached = false
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        c => c.invokeAtRole()
      }
    }

    measure method "invoke role method (cached)" in {
      cached = true
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        c => c.invokeAtRole()
      }
    }

    measure method "call role method directly" in {
      cached = false
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        c => c.invokeDirectly()
      }
    }

    measure method "call role method directly (cached)" in {
      cached = true
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        c => c.invokeDirectly()
      }
    }
  }
}