package scroll.benchmarks

import org.scalameter.api._
import scroll.internal.Compartment
import scroll.internal.graph.{KiamaScalaRoleGraph, ScalaRoleGraph, CachedScalaRoleGraph}
import SCROLLBenchmarkConfig._

trait BenchmarkHelper extends Bench.OfflineReport {
  var backend = JGRAPHT

  protected val NUM_OF_RUNS = 3
  protected val NUM_OF_VMS = 2

  // generators
  protected val roleSizes = Gen.exponential("#Roles")(1, 1000, 10)
  protected val playerSizes = Gen.exponential("#Players")(1, 100, 10)
  protected val input = Gen.crossProduct(playerSizes, roleSizes)

  protected def compartments = for (ps <- playerSizes; rs <- roleSizes) yield createCompartment(ps, rs)

  // mock objects
  class MockRole(id: Int = 0)

  class MockRoleWithFunc {
    def func(): Int = 0
  }

  class MockPlayer(id: Int = 0)

  class MockCompartment(id: Int = 0) extends Compartment {
    plays = backend match {
      case CACHED => new CachedScalaRoleGraph()
      case JGRAPHT => new ScalaRoleGraph()
      case KIAMA => new KiamaScalaRoleGraph()
    }
  }

  def createCompartment(numOfPlayers: Int, numOfRoles: Int) = {
    new Compartment {
      plays = backend match {
        case CACHED => new CachedScalaRoleGraph()
        case JGRAPHT => new ScalaRoleGraph()
        case KIAMA => new KiamaScalaRoleGraph()
      }

      val players = (0 until numOfPlayers).map(id => +new MockPlayer(id))
      val mRole = new MockRoleWithFunc()

      players.foreach(p => {
        (0 until numOfRoles).foreach(p play new MockRole(_))
        p play mRole
      })

      def invokeAtRole() {
        players.foreach(p => {
          val r: Int = p.func()
        })
      }

      def invokeDirectly() {
        players.foreach(p => {
          val r: Int = mRole.func()
        })
      }
    }
  }

  def readFromCSV(path: String, split: String = ",", removeHeadline: Boolean = true): Seq[Seq[String]] = {
    assert(null != path && path.nonEmpty)

    def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
      try {
        f(resource)
      } finally {
        resource.close()
      }

    using(io.Source.fromFile(path))(source => {
      val lines = removeHeadline match {
        case true => source.getLines().toSeq.tail
        case false => source.getLines().toSeq
      }
      return lines.map(l => l.split(split).map(_.trim).toSeq)
    })
  }
}
