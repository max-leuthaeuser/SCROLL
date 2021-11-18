package scroll.benchmarks

import scroll.internal.compartment.impl.Compartment

class CachingExample {

  class Core

  class IntermediateRole(val id: Int)

  class TailingRole(val id: Int) {
    def doSomething(): Int = id
  }

  class SomeCompartment(val isCached: Boolean) extends Compartment {
    roleGraph.reconfigure(cached = isCached, checkForCycles = false)

    def run(): Int = (+core).doSomething()

  }

  private val core = new Core()

  var cachedCompartment: SomeCompartment    = _
  var noncachedCompartment: SomeCompartment = _

  def build(numRoles: Int): CachingExample = {
    val allRoles = (0 until numRoles).map(new IntermediateRole(_)) :+ new TailingRole(numRoles)

    cachedCompartment = new SomeCompartment(true) {
      core play allRoles.head
      allRoles.sliding(2).foreach(l => l(0) play l(1))
    }
    noncachedCompartment = new SomeCompartment(false) {
      core play allRoles.head
      allRoles.sliding(2).foreach(l => l(0) play l(1))
    }
    this
  }

}
