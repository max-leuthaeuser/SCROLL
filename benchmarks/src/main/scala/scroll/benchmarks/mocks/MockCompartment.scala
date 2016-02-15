package scroll.benchmarks.mocks

import scroll.internal.Compartment
import scroll.internal.graph.{KiamaScalaRoleGraph, ScalaRoleGraph, CachedScalaRoleGraph}
import scroll.benchmarks.BenchmarkHelper._

case class MockCompartment(numOfPlayers: Int, numOfRoles: Int, numOfInvokes: Int, backend: Backend, checkForCycles: Boolean) extends Compartment {
  plays = backend match {
    case CACHED => new CachedScalaRoleGraph(checkForCycles)
    case JGRAPHT => new ScalaRoleGraph(checkForCycles)
    case KIAMA => new KiamaScalaRoleGraph(checkForCycles)
  }

  val players = (0 until numOfPlayers).map(id => +MockPlayer(id))
  val mRole = MockRoleWithFunc(numOfRoles)

  players.foreach(p => {
    (0 until numOfRoles).foreach(p play MockRole(_))
    p play mRole
  })

  def invokeAtRole(): Unit = {
    players.foreach(p => {
      (0 until numOfInvokes).foreach(_ => {
        val _: Int = p.func()
      })
    })
  }

  def invokeDirectly(): Unit = {
    players.foreach(p => {
      (0 until numOfInvokes).foreach(_ => mRole.func())
    })
  }
}