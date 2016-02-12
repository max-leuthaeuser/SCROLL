package scroll.benchmarks

import scroll.benchmarks.BenchmarkHelper.{Backend, KIAMA, CACHED, JGRAPHT}
import scroll.benchmarks.mocks.MockCompartment

object RoleGraphExecutionBenchmarks extends App with BenchmarkHelper {

  private val result = StringBuilder.newBuilder

  private def buildCompartments(backend: Backend): List[MockCompartment] = for (ps <- players; rs <- roles) yield MockCompartment(ps, rs, invokes, backend)

  println("Building Compartments ...")
  val (compartmentsJGRAPHT, t1) = buildCompartments(JGRAPHT()).elapsed()
  println("finished in " + t1 + "ns")
  val (compartmentsCACHED, t2) = buildCompartments(CACHED()).elapsed()
  println("finished in " + t2 + "ns")
  val (compartmentsKIAMA, t3) = buildCompartments(KIAMA()).elapsed()
  println("finished in " + t3 + "ns")

  result.append("backend;#player;#roles;time\n")
  println("Running benchmark for JGRAPHT backend ...")
  compartmentsJGRAPHT.foreach(c => {
    result.append(s"JGRAPTH;${c.numOfPlayers};${c.numOfRoles};${c.invokeAtRole().elapsed()}\n")
  })
  println("finished.")

  println("Running benchmark for CACHED backend ...")
  compartmentsCACHED.foreach(c => {
    result.append(s"CACHED;${c.numOfPlayers};${c.numOfRoles};${c.invokeAtRole().elapsed()}\n")
  })
  println("finished.")

  println("Running benchmark for KIAMA backend ...")
  compartmentsKIAMA.foreach(c => {
    result.append(s"KIAMA;${c.numOfPlayers};${c.numOfRoles};${c.invokeAtRole().elapsed()}\n")
  })
  println("finished.")

  println(result.result())
}