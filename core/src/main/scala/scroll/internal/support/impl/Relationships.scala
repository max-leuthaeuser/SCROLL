package scroll.internal.support.impl

import scroll.internal.support.RelationshipsApi
import scroll.internal.support.RoleQueriesApi

import scala.reflect.ClassTag

class Relationships(private[this] val roleQueries: RoleQueriesApi) extends RelationshipsApi {

  import scroll.internal.support.impl.Multiplicities._

  class ToBuilder[L <: AnyRef: ClassTag](name: String, leftMul: Multiplicity) extends ToBuilderApi[L] {

    override def to[R <: AnyRef: ClassTag](rightMul: Multiplicity): RelationshipApi[L, R] =
      new Relationship[L, R](name, leftMul, rightMul)
  }

  class FromBuilder(name: String) extends FromBuilderApi {
    override def from[L <: AnyRef: ClassTag](leftMul: Multiplicity): ToBuilder[L] = new ToBuilder[L](name, leftMul)
  }

  /** Creates a [[Relationships.Relationship]] with the given name
    * with a fluent relationship creation API.
    *
    * @param name the name of the created Relationship
    * @return an instance of the Relationship builder
    */
  override def create(name: String): FromBuilder = new FromBuilder(name)

  /** Class representation of a relationship between two (role) types.
    *
    * @param name     name of the relationship
    * @param leftMul  multiplicity of the left side of the relationship
    * @param rightMul multiplicity of the right side of the relationship
    * @tparam L type of the role of the left side of the relationship
    * @tparam R type of the role of the right side of the relationship
    */
  class Relationship[L <: AnyRef: ClassTag, R <: AnyRef: ClassTag](
    name:     String,
    leftMul:  Multiplicity,
    rightMul: Multiplicity
  ) extends RelationshipApi[L, R] {

    protected val MULT_NOT_ALLOWED: String = "This multiplicity is not allowed!"

    private[this] def checkMul[T](m: Multiplicity, on: Seq[T]): Seq[T] = {
      m match {
        case MMany()                 =>
          assert(on.nonEmpty, s"With left multiplicity for '$name' of '*', the resulting role set should not be empty!")
        case ConcreteValue(v)        =>
          assert(
            v.compare(on.size) == 0,
            s"With a concrete multiplicity for '$name' of '$v' the resulting role set should have the same size!"
          )
        case RangeMultiplicity(f, t) =>
          (f, t) match {
            case (ConcreteValue(v1), ConcreteValue(v2)) =>
              assert(
                v1 <= on.size && v2 >= on.size,
                s"With a multiplicity for '$name' from '$v1' to '$v2', the resulting role set size should be in between!"
              )
            case (ConcreteValue(v), MMany())            =>
              assert(
                v <= on.size,
                s"With a multiplicity for '$name' from '$v' to '*', the resulting role set size should be in between!"
              )
            case _                                      =>
              throw new RuntimeException(MULT_NOT_ALLOWED) // default case
          }
      }
      on
    }

    override def left(matcher: L => Boolean = _ => true): Seq[L] = checkMul(leftMul, roleQueries.all[L](matcher))

    override def right(matcher: R => Boolean = _ => true): Seq[R] = checkMul(rightMul, roleQueries.all[R](matcher))

  }

}
