package scroll.internal.support.impl

import scroll.internal.errors.SCROLLErrors.ConcreteRelationshipMultiplicityViolation
import scroll.internal.errors.SCROLLErrors.EmptyRelationshipMultiplicityViolation
import scroll.internal.errors.SCROLLErrors.RangeRelationshipMultiplicityViolation
import scroll.internal.errors.SCROLLErrors.UnsupportedRelationshipMultiplicity
import scroll.internal.support.RelationshipsApi
import scroll.internal.support.RoleQueriesApi

import scala.reflect.ClassTag

class Relationships(private val roleQueries: RoleQueriesApi) extends RelationshipsApi {

  import scroll.internal.support.impl.Multiplicities._

  class ToBuilder[L <: AnyRef: ClassTag](name: String, leftMul: Multiplicity) extends ToBuilderApi[L] {

    override def to[R <: AnyRef: ClassTag](rightMul: Multiplicity): RelationshipApi[L, R] =
      new Relationship[L, R](name, leftMul, rightMul)

  }

  class FromBuilder(name: String) extends FromBuilderApi {

    override def from[L <: AnyRef: ClassTag](leftMul: Multiplicity): ToBuilder[L] =
      new ToBuilder[L](name, leftMul)

  }

  /** Creates a [[Relationships.Relationship]] with the given name with a fluent relationship creation API.
    *
    * @param name
    *   the name of the created Relationship
    * @return
    *   an instance of the Relationship builder
    */
  override def create(name: String): FromBuilder = new FromBuilder(name)

  /** Class representation of a relationship between two (role) types.
    *
    * @param name
    *   name of the relationship
    * @param leftMul
    *   multiplicity of the left side of the relationship
    * @param rightMul
    *   multiplicity of the right side of the relationship
    * @tparam L
    *   type of the role of the left side of the relationship
    * @tparam R
    *   type of the role of the right side of the relationship
    */
  class Relationship[L <: AnyRef: ClassTag, R <: AnyRef: ClassTag](
    name: String,
    leftMul: Multiplicity,
    rightMul: Multiplicity
  ) extends RelationshipApi[L, R] {

    private def checkMul[T](m: Multiplicity, on: Seq[T]): Seq[T] = {
      m match {
        case MMany() =>
          if (on.isEmpty) {
            throw EmptyRelationshipMultiplicityViolation(name)
          }
        case ConcreteValue(v) =>
          if (v.compare(on.size) != 0) {
            throw ConcreteRelationshipMultiplicityViolation(name, v)
          }
        case RangeMultiplicity(f, t) =>
          (f, t) match {
            case (ConcreteValue(v1), ConcreteValue(v2)) =>
              if (!(v1 <= on.size && v2 >= on.size)) {
                throw RangeRelationshipMultiplicityViolation(name, v1, v2.toString)
              }
            case (ConcreteValue(v), MMany()) =>
              if (!(v <= on.size)) {
                throw RangeRelationshipMultiplicityViolation(name, v, "*")
              }
            case _ =>
              throw UnsupportedRelationshipMultiplicity()
          }
      }
      on
    }

    override def left(matcher: L => Boolean = _ => true): Seq[L] =
      checkMul(leftMul, roleQueries.all[L](matcher))

    override def right(matcher: R => Boolean = _ => true): Seq[R] =
      checkMul(rightMul, roleQueries.all[R](matcher))

  }

}
