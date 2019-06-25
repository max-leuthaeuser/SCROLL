package scroll.tests

import org.scalatest.Suites
import scroll.tests.other.ExamplesTest
import scroll.tests.other.FormalCROMExampleTest
import scroll.tests.other.FormalCROMTest
import scroll.tests.other.SCROLLErrorsTest

class SCROLLTestSuite extends Suites(
  new ExamplesTest(),
  new SCROLLErrorsTest(),
  new FormalCROMExampleTest(),
  new FormalCROMTest(),
  new parameterized.CompartmentMergeTest(),
  new parameterized.CompartmentRoleFeaturesTest(),
  new parameterized.CROITest(),
  new parameterized.DynamicExtensionsTest(),
  new parameterized.ECoreInstanceTest(),
  new parameterized.EqualityRoleTest(),
  new parameterized.FacetsTest(),
  new parameterized.MultiCompartmentMergeTest(),
  new parameterized.MultiCompartmentRoleFeaturesTest(),
  new parameterized.MultiCompartmentTest(),
  new parameterized.MultiRoleFeaturesTest(),
  new parameterized.QueryStrategiesTest(),
  new parameterized.RecursiveBaseCallsWithCaseClassesTest(),
  new parameterized.RecursiveBaseCallsWithClassesTest(),
  new parameterized.RelationshipTest(),
  new parameterized.RoleConstraintsTest(),
  new parameterized.RoleGroupsTest(),
  new parameterized.RolePlayingAutomatonTest(),
  new parameterized.RoleRestrictionsTest(),
  new parameterized.RoleSortingTest(),
  new parameterized.ThrowableInRoleMethodsTest()
)
