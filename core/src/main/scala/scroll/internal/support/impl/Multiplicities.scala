package scroll.internal.support.impl

import scroll.internal.util.Many

/** Provides some predefined multiplicities.
  */
object Multiplicities {

  sealed trait Multiplicity

  sealed trait ExpMultiplicity extends Multiplicity

  final case class MMany() extends ExpMultiplicity

  final case class ConcreteValue(v: Ordered[Int]) extends ExpMultiplicity {
    require(v >= 0)

    def To(t: ExpMultiplicity): Multiplicity = RangeMultiplicity(v, t)
  }

  implicit def orderedToConcreteValue(v: Ordered[Int]): ExpMultiplicity =
    v match {
      case Many() => MMany()
      case _      => ConcreteValue(v)
    }

  implicit def intToConcreteValue(v: Int): ConcreteValue = ConcreteValue(v)

  final case class RangeMultiplicity(from: ExpMultiplicity, to: ExpMultiplicity) extends Multiplicity

}
