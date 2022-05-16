package scroll.internal.support

import scala.reflect.ClassTag

/** Allows to add and check role relationships to a compartment instance.
  */
trait RelationshipsApi {

  import scroll.internal.support.impl.Multiplicities._

  abstract class ToBuilderApi[L <: AnyRef: ClassTag] {
    def to[R <: AnyRef: ClassTag](rightMul: Multiplicity): RelationshipApi[L, R]
  }

  abstract class FromBuilderApi {
    def from[L <: AnyRef: ClassTag](leftMul: Multiplicity): ToBuilderApi[L]
  }

  /** Creates a [[RelationshipApi]] with the given name with a fluent relationship creation API.
    *
    * @param name
    *   the name of the created Relationship
    * @return
    *   an instance of the Relationship builder
    */
  def create(name: String): FromBuilderApi

  /** Representation of a relationship between two (role) types.
    *
    * @tparam L
    *   type of the role of the left side of the relationship
    * @tparam R
    *   type of the role of the right side of the relationship
    */
  abstract class RelationshipApi[L <: AnyRef: ClassTag, R <: AnyRef: ClassTag] {

    /** Get all instances of the left side of the relationship w.r.t. the provided matching function and checking the
      * multiplicity.
      *
      * @param matcher
      *   a matching function to select the appropriate instances
      * @return
      *   all instances of the left side of the relationship w.r.t. the provided matching function.
      */
    def left(matcher: L => Boolean = _ => true): Seq[L]

    /** Get all instances of the right side of the relationship w.r.t. the provided matching function and checking the
      * multiplicity.
      *
      * @param matcher
      *   a matching function to select the appropriate instances
      * @return
      *   all instances of the right side of the relationship w.r.t. the provided matching function.
      */
    def right(matcher: R => Boolean = _ => true): Seq[R]

  }

}
