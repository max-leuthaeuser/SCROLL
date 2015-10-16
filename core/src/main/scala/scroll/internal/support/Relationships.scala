package scroll.internal.support

import scroll.internal.Compartment

trait Relationships {
  self: Compartment =>

  import Relationship._

  object Relationship {

    abstract class Multiplicity

    abstract class ExpMultiplicity extends Multiplicity

    case class Many() extends ExpMultiplicity

    case class ConcreteValue(v: Int) extends ExpMultiplicity {
      require(v >= 0)

      def To(t: ExpMultiplicity): Multiplicity = RangeMultiplicity(v, t)
    }

    implicit def intToConcreteValue(v: Int): ConcreteValue = new ConcreteValue(v)

    case class RangeMultiplicity(from: ConcreteValue, to: ExpMultiplicity) extends Multiplicity

    def apply(name: String) = new {
      def from[L: Manifest](leftMul: Multiplicity) = new {
        def to[R: Manifest](rightMul: Multiplicity): Relationship[L, R] = new Relationship(name, leftMul, rightMul)
      }
    }

  }

  class Relationship[L: Manifest, R: Manifest](name: String,
                                               var leftMul: Multiplicity,
                                               var rightMul: Multiplicity) {

    private def checkMul[T](m: Multiplicity, on: Seq[T]): Seq[T] = {
      m match {
        case Many() => assert(on.nonEmpty, s"With left multiplicity for '$name' of '*', the resulting role set should not be empty!")
        case ConcreteValue(v) => assert(on.size == v, s"With a concrete multiplicity for '$name' of '$v' the resulting role set should have the same size!")
        case RangeMultiplicity(f, t) => (f, t) match {
          case (ConcreteValue(v1), ConcreteValue(v2)) => assert(v1 <= on.size && on.size <= v2, s"With a multiplicity for '$name' from '$v1' to '$v2', the resulting role set size should be in between!")
          case (ConcreteValue(v), Many()) => assert(v <= on.size, s"With a multiplicity for '$name' from '$v' to '*', the resulting role set size should be in between!")
          case _ => throw new RuntimeException("This multiplicity is not allowed!") // default case
        }
        case _ => throw new RuntimeException("This multiplicity is not allowed!") // default case
      }
      on
    }

    def left(matcher: L => Boolean = _ => true): Seq[L] = checkMul(leftMul, all[L](matcher))

    def right(matcher: R => Boolean = _ => true): Seq[R] = checkMul(rightMul, all[R](matcher))

  }

}
