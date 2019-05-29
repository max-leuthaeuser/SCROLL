package scroll.tests

import org.scalatest.BeforeAndAfter
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.featurespec.AnyFeatureSpec
import scroll.tests.mocks.SomeCompartment
import scroll.tests.mocks.SomeMultiCompartment

abstract class AbstractSCROLLTest(cached: Boolean) extends AnyFeatureSpec with GivenWhenThen with Matchers with BeforeAndAfter {

  protected def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

  class CompartmentUnderTest() extends SomeCompartment(cached)

  class MultiCompartmentUnderTest() extends SomeMultiCompartment(cached)

  before {
    info(s"Running SCROLL test '${this.getClass}' with cache = '$cached':")
  }

}
