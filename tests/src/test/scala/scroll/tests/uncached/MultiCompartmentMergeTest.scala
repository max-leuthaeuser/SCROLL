package scroll.tests.uncached

import scroll.tests.cached

class MultiCompartmentMergeTest extends cached.MultiCompartmentMergeTest {
  override val cached = false
}
