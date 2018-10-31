package scroll.tests.mocks

import scroll.internal.MultiCompartment
import scroll.internal.graph.ScalaRoleGraphBuilder

class SomeMultiCompartment(cached: Boolean) extends MultiCompartment {

  ScalaRoleGraphBuilder.cached(cached)

  class RoleA {

    def i(): Int = 1
    
    def is(): Seq[Int] = Seq(1, 1)

    def s(): String = "a"
  }

  class RoleB {

    def i(): Int = 2
    
    def is(): Seq[Int] = Seq(2, 2)

    def s(): String = "b"
  }

  class RoleC {

    def i(): Int = 3
    
    def is(): Seq[Int] = Seq(3, 3)

    def s(): String = "c"
  }

  class RoleD {

    def i(): Int = 4
    
    def is(): Seq[Int] = Seq(4, 4)

    def s(): String = "d"
  }

}
