package scroll.tests.mocks

import scroll.internal.MultiCompartment

class SomeMultiCompartment(isCached: Boolean) extends MultiCompartment {
  reconfigure(cached = isCached, checkForCycles = true)
}