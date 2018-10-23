package scroll.tests.mocks

import scroll.internal.MultiCompartment
import scroll.internal.graph.ScalaRoleGraphBuilder

class SomeMultiCompartment(cached: Boolean) extends MultiCompartment {

  ScalaRoleGraphBuilder.cached(cached)

  class RoleA {

    def i(): Int = 1

    def s(): String = "a"
  }

  class RoleB {

    def i(): Int = 2

    def s(): String = "b"
  }

  class RoleC {

    def i(): Int = 3

    def s(): String = "c"
  }

  class RoleD {

    def i(): Int = 4

    def s(): String = "d"
  }

}
