package scroll.internal.support

import scroll.internal.Compartment
import scroll.internal.util.Many
import scala.reflect.ClassTag

/**
  * Allows to add and check role relationships to a compartment instance.
  */
trait Relationships {
  self: Compartment =>

  import Relationship._

  /**
    * Companion object for [[scroll.internal.support.Relationships.Relationship]] providing
    * some predefined multiplicities and a fluent relationship creation API.
    */
  object Relationship {

    sealed trait Multiplicity

    trait ExpMultiplicity extends Multiplicity

    case class MMany() extends ExpMultiplicity

    case class ConcreteValue(v: Ordered[Int]) extends ExpMultiplicity {
      require(v >= 0)

      def To(t: ExpMultiplicity): Multiplicity = RangeMultiplicity(v, t)
    }

    implicit def orderedToConcreteValue(v: Ordered[Int]): ExpMultiplicity = v match {
      case Many() => MMany()
      case _ => ConcreteValue(v)
    }

    implicit def intToConcreteValue(v: Int): ConcreteValue = ConcreteValue(v)

    case class RangeMultiplicity(from: ExpMultiplicity, to: ExpMultiplicity) extends Multiplicity

    def apply(name: String) = new {
      def from[L: ClassTag](leftMul: Multiplicity) = new {
        def to[R: ClassTag](rightMul: Multiplicity): Relationship[L, R] = new Relationship(name, leftMul, rightMul)
      }
    }

  }

  /**
    * Class representation of a relationship between two (role) types.
    *
    * @param name     name of the relationship
    * @param leftMul  multiplicity of the left side of the relationship
    * @param rightMul multiplicity of the right side of the relationship
    * @tparam L type of the role of the left side of the relationship
    * @tparam R type of the role of the right side of the relationship
    */
  class Relationship[L: ClassTag, R: ClassTag](name: String,
                                               var leftMul: Multiplicity,
                                               var rightMul: Multiplicity) {

    private def checkMul[T](m: Multiplicity, on: Seq[T]): Seq[T] = {
      m match {
        case MMany() => assert(on.nonEmpty, s"With left multiplicity for '$name' of '*', the resulting role set should not be empty!")
        case ConcreteValue(v) => assert(v.compare(on.size) == 0, s"With a concrete multiplicity for '$name' of '$v' the resulting role set should have the same size!")
        case RangeMultiplicity(f, t) => (f, t) match {
          case (ConcreteValue(v1), ConcreteValue(v2)) => assert(v1 <= on.size && v2 >= on.size, s"With a multiplicity for '$name' from '$v1' to '$v2', the resulting role set size should be in between!")
          case (ConcreteValue(v), MMany()) => assert(v <= on.size, s"With a multiplicity for '$name' from '$v' to '*', the resulting role set size should be in between!")
          case _ => throw new RuntimeException("This multiplicity is not allowed!") // default case
        }
        case _ => throw new RuntimeException("This multiplicity is not allowed!") // default case
      }
      on
    }

    /**
      * Get all instances of the left side of the relationship w.r.t. the provided matching function and checking the multiplicity.
      *
      * @param matcher a matching function to select the appropriate instances
      * @return all instances of the left side of the relationship w.r.t. the provided matching function.
      */
    def left(matcher: L => Boolean = _ => true): Seq[L] = checkMul(leftMul, all[L](matcher))

    /**
      * Get all instances of the right side of the relationship w.r.t. the provided matching function and checking the multiplicity.
      *
      * @param matcher a matching function to select the appropriate instances
      * @return all instances of the right side of the relationship w.r.t. the provided matching function.
      */
    def right(matcher: R => Boolean = _ => true): Seq[R] = checkMul(rightMul, all[R](matcher))

  }

}
