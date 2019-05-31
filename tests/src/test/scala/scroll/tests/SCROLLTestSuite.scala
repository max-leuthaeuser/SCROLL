package scroll.tests

import org.scalatest.featurespec.AnyFeatureSpecLike
import org.scalatest.ConfigMapWrapperSuite
import org.scalatest.Suite
import org.scalatest.Suites
import org.scalatest.WrapWith
import scroll.tests.SCROLLTestSuite.SCROLLParameterizedSuites
import scroll.tests.SCROLLTestSuite.SCROLLStandardSuites

object SCROLLTestSuite {

  abstract class SCROLLTestSuites(suites: AbstractSCROLLTest*) extends Suites(suites: _*)

  abstract class ParameterizedSuites[T](param: T, suites: AbstractSCROLLTest*) extends SCROLLTestSuites(suites: _*)

  class SCROLLStandardSuites extends SCROLLTestSuites(
    new ExamplesTest(),
    new SCROLLErrorsTest()
  )

  class SCROLLParameterizedSuites(param: Boolean) extends ParameterizedSuites[Boolean](
    param,
    new CompartmentRoleFeaturesTest(cached = param),
    new MultiCompartmentRoleFeaturesTest(cached = param),
    new RoleSortingTest(cached = param),
    new DynamicExtensionsTest(cached = param),
    new EqualityRoleTest(cached = param),
    new RelationshipTest(cached = param),
    new UnionTypesTest(cached = param),
    new FormalCROMTest(cached = param),
    new FormalCROMExampleTest(cached = param),
    new ECoreInstanceTest(cached = param),
    new CROITest(cached = param),
    new RoleConstraintsTest(cached = param),
    new RolePlayingAutomatonTest(cached = param),
    new RoleRestrictionsTest(cached = param),
    new RoleGroupsTest(cached = param),
    new MultiRoleFeaturesTest(cached = param),
    new FacetsTest(cached = param),
    new RecursiveBaseCallsWithClassesTest(cached = param),
    new RecursiveBaseCallsWithCaseClassesTest(cached = param),
    new ThrowableInRoleMethodsTest(cached = param),
    new MultiCompartmentTest(cached = param),
    new MultiCompartmentMergeTest(cached = param),
    new CompartmentMergeTest(cached = param),
    new QueryStrategiesTest(cached = param)
  )

}

@WrapWith(classOf[ConfigMapWrapperSuite])
class SCROLLTestSuite(configMap: Map[String, Any]) extends Suites with AnyFeatureSpecLike {
  override val nestedSuites: IndexedSeq[Suite] = configMap.getOrElse("cached", "").toString.toBooleanOption match {
    case Some(c: Boolean) =>
      alert(s"Got value '$c' for parameter 'cached' from Scalatest configMap.")
      Vector(
        new SCROLLStandardSuites(),
        new SCROLLParameterizedSuites(c)
      )
    case e =>
      alert(s"Got no valid value for parameter 'cached' from Scalatest configMap. (Got: '$e')")
      alert("Running tests both with 'true' and 'false'.")
      Vector(
        new SCROLLStandardSuites(),
        new SCROLLParameterizedSuites(true),
        new SCROLLParameterizedSuites(false)
      )
  }
}


