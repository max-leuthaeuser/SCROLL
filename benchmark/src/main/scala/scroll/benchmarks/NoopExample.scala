package scroll.benchmarks

import scroll.internal.compartment.impl.Compartment
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery._

class NoopExample(cached: Boolean) {

  val compartment = new NoopCompartment()

  class BaseType {
    def noArgs(): AnyRef = this

    def referenceArgAndReturn(o: AnyRef): AnyRef = o

    /**
      * Primitive argument and return types probably provoke autoboxing when a role is bound.
      */
    def primitiveArgsAndReturn(x: Int, y: Int): Int = x + y
  }

  class NoopCompartment extends Compartment {
    roleGraph.reconfigure(cached = cached, checkForCycles = false)

    val player = new BaseType() play new NoopRole()

    /**
      * No-op role methods which just forward to the base
      */
    class NoopRole {
      given DispatchQuery = Bypassing(_.isInstanceOf[NoopRole])

      def noArgs(): AnyRef = {
        (+this).noArgs()
      }

      def referenceArgAndReturn(o: AnyRef): AnyRef = {
        (+this).referenceArgAndReturn(o)
      }

      def primitiveArgsAndReturn(x: Int, y: Int): Int = {
        (+this).primitiveArgsAndReturn(x, y)
      }
    }

  }

}
