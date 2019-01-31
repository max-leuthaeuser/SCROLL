package scroll.benchmarks

import scroll.internal.support.DispatchQuery._
import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery

class NoopExample(cached: Boolean) {

  class BaseType {
    def noArgs(): AnyRef = this

    def referenceArgAndReturn(o: AnyRef): AnyRef = o

    /**
      * Primitive argument and return types probably provoke autoboxing when a role is bound.
      */
    def primitiveArgsAndReturn(x: Int, y: Int): Int = x + y
  }

  trait NoopCompartment extends Compartment {
    reconfigure(cached = cached, checkForCycles = false)

    /**
      * No-op role methods which just forward to the base
      */
    class NoopRole {
      implicit val dd: DispatchQuery = Bypassing(_.isInstanceOf[NoopRole])

      def noArgs(): AnyRef = {
        +this noArgs()
      }

      def referenceArgAndReturn(o: AnyRef): AnyRef = {
        +this referenceArgAndReturn o
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
