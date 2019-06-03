package scroll.tests

import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scroll.tests.mocks.SomeCompartment
import scroll.tests.mocks.SomeMultiCompartment

abstract class AbstractSCROLLTest extends AnyFeatureSpec with GivenWhenThen with Matchers {
  protected val cached: Boolean = true

  protected def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

  class CompartmentUnderTest() extends SomeCompartment(cached)

  class MultiCompartmentUnderTest() extends SomeMultiCompartment(cached)

}
