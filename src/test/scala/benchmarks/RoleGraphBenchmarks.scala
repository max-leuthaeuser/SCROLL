package benchmarks

import org.scalameter.api._
import scroll.internal.Compartment

object RoleGraphBenchmarks extends Bench.OfflineReport {
  val NUM_OF_RUNS = 10
  val NUM_OF_VMS = 3

  // mock objects
  class MockRole(id: Int = 0)

  class MockPlayer(id: Int = 0)

  class MockCompartment(id: Int = 0) extends Compartment

  // generators
  val roleSizes = Gen.exponential("#Roles")(1, 1000, 10)
  val playerSizes = Gen.exponential("#Players")(1, 1000, 10)

  val input = Gen.crossProduct(playerSizes, roleSizes)

  // actual benchmarks
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
  }
}