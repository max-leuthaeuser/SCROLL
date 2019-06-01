package scroll.tests.uncached

import scroll.tests.cached

class CompartmentMergeTest extends cached.CompartmentMergeTest {
  override val cached = false
}