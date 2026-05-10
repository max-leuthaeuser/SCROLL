/** Public SCROLL facade.
  *
  * Import types and helpers from this package when using the library directly. The implementation continues to live
  * under `scroll.internal`, but user code should prefer the stable aliases and re-exports defined here.
  */
package object scroll {

  /** Public alias for core SCROLL dispatch and lookup errors. */
  type SCROLLError = internal.errors.SCROLLErrors.SCROLLError

  /** Public alias for raw multi-dispatch return values.
    *
    * A multi-dispatch call can fail before dispatch starts or per dispatched role invocation, so the result keeps both
    * error layers intact.
    */
  type MultiDispatchResult[E] = Either[SCROLLError, Seq[Either[SCROLLError, E]]]

  /** Public alias for the standard single-dispatch compartment DSL. */
  type Compartment = internal.compartment.impl.Compartment

  /** Public alias for the multi-dispatch compartment DSL. */
  type MultiCompartment = internal.compartment.impl.MultiCompartment

  /** Public alias for composed dispatch queries. */
  type DispatchQuery = internal.dispatch.DispatchQuery

  /** Public alias for the unbounded role-group cardinality marker. */
  type Many = internal.util.Many

  /** Convenience helpers for consuming multi-dispatch results without changing the underlying dynamic API.
    */
  extension [E](result: MultiDispatchResult[E])

    /** Sequence all per-role results into a single `Either`.
      *
      * Returns the first encountered [[SCROLLError]] or the collected successful values if all dispatched role
      * invocations succeed.
      */
    def sequenceResults: Either[SCROLLError, Seq[E]] =
      result.flatMap(_.foldRight(Right(Seq.empty[E]): Either[SCROLLError, Seq[E]]) { (entry, acc) =>
        for {
          value  <- entry
          values <- acc
        } yield value +: values
      })

}
