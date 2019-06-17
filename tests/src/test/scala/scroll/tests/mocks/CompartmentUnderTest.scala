package scroll.tests.mocks

import scroll.internal.Compartment

class CompartmentUnderTest(cached: Boolean, checkForCycles: Boolean) extends Compartment {
  reconfigure(cached, checkForCycles)
}
