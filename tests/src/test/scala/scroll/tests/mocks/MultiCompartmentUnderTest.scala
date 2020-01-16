package scroll.tests.mocks

import scroll.internal.compartment.impl.MultiCompartment

class MultiCompartmentUnderTest(cached: Boolean, checkForCycles: Boolean) extends MultiCompartment {
  roleGraph.reconfigure(cached, checkForCycles)
}
