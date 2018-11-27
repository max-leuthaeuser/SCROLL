package scroll.tests.mocks

import scroll.internal.MultiCompartment
import scroll.internal.graph.ScalaRoleGraphBuilder

class SomeMultiCompartment(cached: Boolean) extends MultiCompartment {

  ScalaRoleGraphBuilder.cached(cached)

}
