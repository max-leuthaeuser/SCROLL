package scroll.tests.mocks

import scroll.internal.Compartment
import scroll.internal.graph.ScalaRoleGraphBuilder

class SomeCompartment(cached: Boolean) extends Compartment {

  ScalaRoleGraphBuilder.cached(cached)

}
