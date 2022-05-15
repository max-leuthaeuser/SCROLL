package scroll.tests

import org.scalatest.Suites

class SCROLLTestSuite
    extends Suites(
      new other.ExamplesTest,
      new other.SCROLLErrorsTest,
      new other.FormalCROMExampleTest,
      new other.FormalCROMTest,
      new parameterized.BuiltinsTest,
      new parameterized.CompartmentMergeTest,
      new parameterized.CompartmentRoleFeaturesTest,
      new parameterized.CROITest,
      new parameterized.DynamicExtensionsTest,
      new parameterized.ECoreInstanceTest,
      new parameterized.EqualityRoleTest,
      new parameterized.FacetsTest,
      new parameterized.MultiCompartmentMergeTest,
      new parameterized.MultiCompartmentRoleFeaturesTest,
      new parameterized.MultiCompartmentTest,
      new parameterized.MultiRoleFeaturesTest,
      new parameterized.QueryStrategiesTest,
      new parameterized.RecursiveBaseCallsWithCaseClassesTest,
      new parameterized.RecursiveBaseCallsWithClassesTest,
      new parameterized.RelationshipTest,
      new parameterized.RoleConstraintsTest,
      new parameterized.RoleGroupsTest,
      new parameterized.RolePlayingAutomatonTest,
      new parameterized.RoleRestrictionsTest,
      new parameterized.RoleSortingTest,
      new parameterized.ThrowableInRoleMethodsTest
    )
