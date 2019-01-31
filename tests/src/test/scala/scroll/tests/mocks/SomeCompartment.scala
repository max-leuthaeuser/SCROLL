package scroll.tests.mocks

import scroll.internal.Compartment

class SomeCompartment(isCached: Boolean) extends Compartment {
  reconfigure(cached = isCached, checkForCycles = true)
}
