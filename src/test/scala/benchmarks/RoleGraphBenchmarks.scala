package benchmarks

import org.scalameter.api._
import scroll.internal.Compartment

object RoleGraphBenchmarks extends Bench.OfflineReport {
  val NUM_OF_RUNS = 10
  val NUM_OF_VMS = 3

  // mock objects
  class MockRole(id: Int = 0)

  class MockRoleWithFunc {
    def func() {}
  }

  class MockPlayer(id: Int = 0)

  class MockCompartment(id: Int = 0) extends Compartment

  // generators
  val roleSizes = Gen.exponential("#Roles")(1, 100, 10)
  val playerSizes = Gen.exponential("#Players")(1, 1000, 10)
  val input = Gen.crossProduct(playerSizes, roleSizes)
  val compartments = (for (ps <- playerSizes; rs <- roleSizes) yield createCompartment(ps, rs)).cached

  // helper methods/factories
  def createCompartment(numOfPlayers: Int, numOfRoles: Int): Compartment {def invoke()} = {
    new Compartment {
      val players = (0 until numOfPlayers).map(id => +new MockPlayer(id))

      players.foreach(p => {
        (0 until numOfRoles).foreach(p play new MockRole(_))
        p play new MockRoleWithFunc()
      })

      def invoke() {
        players.foreach(p => {
          p.func()
          "Done" // TODO: why is it required to return something here?
        })
      }
    }
  }

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