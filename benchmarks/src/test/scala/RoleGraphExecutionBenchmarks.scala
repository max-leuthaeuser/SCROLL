import org.scalameter.api._

trait RoleGraphExecutionBenchmarks extends BenchmarkHelper {
  performance of "RoleGraph" in {
    measure method "play" in {
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
      using(compartments) config(
        exec.benchRuns -> NUM_OF_RUNS,
        exec.independentSamples -> NUM_OF_VMS
        ) in {
        c => c.invoke()
      }
    }
  }
}