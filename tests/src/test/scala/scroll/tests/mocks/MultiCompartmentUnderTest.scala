package scroll.tests.mocks

import scroll.internal.MultiCompartment

class MultiCompartmentUnderTest(cached: Boolean, checkForCycles: Boolean) extends MultiCompartment {
  reconfigure(cached, checkForCycles)
}
