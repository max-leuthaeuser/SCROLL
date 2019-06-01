package scroll.tests.uncached

import scroll.tests.cached

class DynamicExtensionsTest extends cached.DynamicExtensionsTest {
  override val cached = false
}
