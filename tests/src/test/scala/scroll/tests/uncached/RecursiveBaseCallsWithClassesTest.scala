package scroll.tests.uncached

import scroll.tests.cached

class RecursiveBaseCallsWithClassesTest extends cached.RecursiveBaseCallsWithClassesTest {
  override val cached = false
}
