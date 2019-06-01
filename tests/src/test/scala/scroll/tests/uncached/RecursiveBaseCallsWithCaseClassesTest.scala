package scroll.tests.uncached

import scroll.tests.cached

class RecursiveBaseCallsWithCaseClassesTest extends cached.RecursiveBaseCallsWithCaseClassesTest {
  override val cached = false
}
