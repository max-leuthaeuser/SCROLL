package scroll.tests.mocks

import scroll.internal.compartment.impl.Compartment

class CompartmentUnderTest(cached: Boolean, checkForCycles: Boolean) extends Compartment {
  roleGraph.reconfigure(cached, checkForCycles)
}
