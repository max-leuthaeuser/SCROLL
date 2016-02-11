package scroll.benchmarks

import org.scalameter.api._
import scroll.internal.Compartment
import scroll.internal.graph.{KiamaScalaRoleGraph, ScalaRoleGraph, CachedScalaRoleGraph}

trait BenchmarkHelper extends Bench.OfflineReport {

  sealed trait Backend

  case class JGRAPHT() extends Backend

  case class KIAMA() extends Backend

  case class CACHED() extends Backend

  protected val NUM_OF_RUNS = 3
  protected val NUM_OF_VMS = 2
  protected val MIN_WARMUPS = 1
  protected val MAX_WARMUPS = 3
  protected val JVM_FLAGS = List("-Xms4g", "-Xmx4g")

  // generators
  protected val roleSizes = Gen.exponential("#Roles")(1, 1000, 10)
  protected val playerSizes = Gen.exponential("#Players")(1, 100, 10)
  protected val input = Gen.crossProduct(playerSizes, roleSizes)

  protected val compartmentsJGRAPHT = (for (ps <- playerSizes; rs <- roleSizes) yield new InvokeCompartment(ps, rs, JGRAPHT())).cached

  protected val compartmentsCACHED = (for (ps <- playerSizes; rs <- roleSizes) yield new InvokeCompartment(ps, rs, CACHED())).cached

  protected val compartmentsKIAMA = (for (ps <- playerSizes; rs <- roleSizes) yield new InvokeCompartment(ps, rs, KIAMA())).cached

  // mock objects
  case class MockRole(id: Int = 0)

  case class MockRoleWithFunc(id: Int = 0) {
    def func(): Int = 0
  }

  case class MockPlayer(id: Int = 0)

  case class MockCompartment(id: Int = 0, backend: Backend) extends Compartment {
    plays = backend match {
      case CACHED() => new CachedScalaRoleGraph()
      case JGRAPHT() => new ScalaRoleGraph()
      case KIAMA() => new KiamaScalaRoleGraph()
    }
  }

  class InvokeCompartment(numOfPlayers: Int, numOfRoles: Int, backend: Backend) extends Compartment {
    plays = backend match {
      case CACHED() => new CachedScalaRoleGraph()
      case JGRAPHT() => new ScalaRoleGraph()
      case KIAMA() => new KiamaScalaRoleGraph()
    }

    val players = (0 until numOfPlayers).map(id => +MockPlayer(id))
    val mRole = MockRoleWithFunc(numOfRoles)

    players.foreach(p => {
      (0 until numOfRoles).foreach(p play MockRole(_))
      p play mRole
    })

    def invokeAtRole() {
      players.foreach(p => {
        (0 until 100).foreach(_ => {
          val r: Int = p.func()
        })
      })
    }

    def invokeDirectly() {
      players.foreach(p => {
        (0 until 100).foreach(_ => mRole.func())
      })
    }
  }

}
