package scroll.tests.uncached

import scroll.tests.cached

class MultiCompartmentTest extends cached.MultiCompartmentTest {
  override val cached = false
}
