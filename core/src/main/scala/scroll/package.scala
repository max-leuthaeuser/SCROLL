/** Public SCROLL facade.
  *
  * Import types and helpers from this package when using the library directly. The implementation continues to live
  * under `scroll.internal`, but user code should prefer the stable aliases and re-exports defined here.
  */
package object scroll {

  /** Public alias for the standard single-dispatch compartment DSL. */
  type Compartment = internal.compartment.impl.Compartment

  /** Public alias for the multi-dispatch compartment DSL. */
  type MultiCompartment = internal.compartment.impl.MultiCompartment

  /** Public alias for composed dispatch queries. */
  type DispatchQuery = internal.dispatch.DispatchQuery

  /** Public alias for the unbounded role-group cardinality marker. */
  type Many = internal.util.Many

}
