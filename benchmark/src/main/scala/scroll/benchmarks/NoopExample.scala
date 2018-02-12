package scroll.benchmarks

import scroll.internal.support.DispatchQuery._
import scroll.internal.Compartment
import scroll.internal.graph.{CachedScalaRoleGraph, ScalaRoleGraph}

class NoopExample(cached: Boolean) {
  class BaseType {
    def noArgs(): AnyRef = this

    def referenceArgAndReturn(o: AnyRef): AnyRef = o

    def primitiveArgsAndReturn(x: Int, y: Int): Int = x + y
  }

  trait NoopCompartment extends Compartment {
    override val plays = if (cached) {
      new CachedScalaRoleGraph(checkForCycles = false)
    } else {
      new ScalaRoleGraph(checkForCycles = false)
    }

    class NoopRole {
      implicit val dd = Bypassing(_.isInstanceOf[NoopRole])

      def noArgs(): AnyRef = {
        +this noArgs()
      }

      def referenceArgAndReturn(o: AnyRef): AnyRef = {
        +this referenceArgAndReturn(o)
      }

      def primitiveArgsAndReturn(x: Int, y: Int): Int = {
        +this primitiveArgsAndReturn(x, y)
      }
    }
  }

  val compartment = new NoopCompartment {
    val player = new BaseType() play new NoopRole()
  }
}
