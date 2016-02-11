package scroll.benchmarks

import scroll.benchmarks.BenchmarkHelper.{KIAMA, CACHED, JGRAPHT}
import scroll.benchmarks.mocks.MockCompartment

object RoleGraphExecutionBenchmarks extends App with BenchmarkHelper {

  private val result = StringBuilder.newBuilder

  println("Building Compartments ...")
  val compartmentsJGRAPHT = for (ps <- players; rs <- roles) yield MockCompartment(ps, rs, invokes, JGRAPHT())
  val compartmentsCACHED = for (ps <- players; rs <- roles) yield MockCompartment(ps, rs, invokes, CACHED())
  val compartmentsKIAMA = for (ps <- players; rs <- roles) yield MockCompartment(ps, rs, invokes, KIAMA())
  println("finished.")

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