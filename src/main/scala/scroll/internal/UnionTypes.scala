package scroll.internal

object UnionTypes {

  /**
   * Implements union types in unmodified Scala via Curry-Howard isomorphism. 
   * see: http://www.chuusai.com/2011/06/09/scala-union-types-curry-howard/
   */
  trait CurryHowardUnionTypes {

    import shapeless._

    type ¬[A] = A => Nothing
    type ¬¬[A] = ¬[¬[A]]
    type ∨[T, U] = ¬[¬[T] with ¬[U]]
    type |∨|[T, U] = {type λ[X] = ¬¬[X] <:< (T ∨ U)}
    type ¬|∨|[T, U] = {type λ[X] = ¬¬[X] <:!< (T ∨ U)}

  }

  /**
   * Just some renaming.
   */
  trait RoleUnionTypes extends CurryHowardUnionTypes {

    type or[T, U] = |∨|[T, U]
    type nor[T, U] = ¬|∨|[T, U]

  }

}
