package scroll.internal.support

import shapeless._

/**
  * Object containing union types in unmodified Scala via Curry-Howard isomorphism.
  */
object UnionTypes {

  /**
    * Implements union types in unmodified Scala via Curry-Howard isomorphism.
    * see: [[http://www.chuusai.com/2011/06/09/scala-union-types-curry-howard/]].
    */
  trait CurryHowardUnionTypes {
    // scalastyle:off
    type ¬[A] = A => Nothing
    type ¬¬[A] = ¬[¬[A]]
    type ∨[T, U] = ¬[¬[T] with ¬[U]]
    type |∨|[T, U] = {type λ[X] = ¬¬[X] <:< (T ∨ U)}
    type ¬|∨|[T, U] = {type λ[X] = ¬¬[X] <:!< (T ∨ U)}
    // scalastyle:on
  }

  /**
    * Just some renaming.
    */
  trait RoleUnionTypes extends CurryHowardUnionTypes {
    // scalastyle:off
    type or[T, U] = |∨|[T, U]
    type nor[T, U] = ¬|∨|[T, U]
    // scalastyle:on
  }

}
