package scroll.tests

import org.scalatest.BeforeAndAfter
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scroll.tests.mocks.SomeCompartment
import scroll.tests.mocks.SomeMultiCompartment

abstract class AbstractSCROLLTest(cached: Boolean) extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfter {

  class CompartmentUnderTest() extends SomeCompartment(cached)

  class MultiCompartmentUnderTest() extends SomeMultiCompartment(cached)

  before {
    info(s"Running SCROLL test '${this.getClass}' with cache = '$cached':")
  }

}
