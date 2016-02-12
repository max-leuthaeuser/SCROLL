package scroll.benchmarks.mocks

import scroll.internal.Compartment
import scroll.internal.graph.{KiamaScalaRoleGraph, ScalaRoleGraph, CachedScalaRoleGraph}
import scroll.benchmarks.BenchmarkHelper._

case class MockCompartment(numOfPlayers: Int, numOfRoles: Int, numOfInvokes: Int, backend: Backend) extends Compartment {
  plays = backend match {
    case CACHED() => new CachedScalaRoleGraph()
    case JGRAPHT() => new ScalaRoleGraph()
    case KIAMA() => new KiamaScalaRoleGraph(checkForCycles = false)
  }

  val players = (0 until numOfPlayers).map(id => +MockPlayer(id))
  val mRole = MockRoleWithFunc(numOfRoles)

  players.foreach(p => {
    (0 until numOfRoles).foreach(p play MockRole(_))
    p play mRole
  })

  def invokeAtRole() {
    players.foreach(p => {
      (0 until numOfInvokes).foreach(_ => {
        val r: Int = p.func()
      })
    })
  }

  def invokeDirectly() {
    players.foreach(p => {
      (0 until numOfInvokes).foreach(_ => mRole.func())
    })
  }
}