package scroll.tests

import org.scalatest.Suites
import scroll.tests.other._

class SCROLLTestSuite extends Suites(
  new ExamplesTest(),
  new SCROLLErrorsTest(),
  new FormalCROMExampleTest(),
  new FormalCROMTest(),
  new cached.CompartmentMergeTest(),
  new cached.CompartmentRoleFeaturesTest(),
  new cached.CROITest(),
  new cached.DynamicExtensionsTest(),
  new cached.ECoreInstanceTest(),
  new cached.EqualityRoleTest(),
  new cached.FacetsTest(),
  new cached.MultiCompartmentMergeTest(),
  new cached.MultiCompartmentRoleFeaturesTest(),
  new cached.MultiCompartmentTest(),
  new cached.MultiRoleFeaturesTest(),
  new cached.QueryStrategiesTest(),
  new cached.RecursiveBaseCallsWithCaseClassesTest(),
  new cached.RecursiveBaseCallsWithClassesTest(),
  new cached.RelationshipTest(),
  new cached.RoleConstraintsTest(),
  new cached.RoleGroupsTest(),
  new cached.RolePlayingAutomatonTest(),
  new cached.RoleRestrictionsTest(),
  new cached.RoleSortingTest(),
  new cached.ThrowableInRoleMethodsTest(),
  new cached.UnionTypesTest(),
  new uncached.CompartmentMergeTest(),
  new uncached.CompartmentRoleFeaturesTest(),
  new uncached.CROITest(),
  new uncached.DynamicExtensionsTest(),
  new uncached.ECoreInstanceTest(),
  new uncached.EqualityRoleTest(),
  new uncached.FacetsTest(),
  new uncached.MultiCompartmentMergeTest(),
  new uncached.MultiCompartmentRoleFeaturesTest(),
  new uncached.MultiCompartmentTest(),
  new uncached.MultiRoleFeaturesTest(),
  new uncached.QueryStrategiesTest(),
  new uncached.RecursiveBaseCallsWithCaseClassesTest(),
  new uncached.RecursiveBaseCallsWithClassesTest(),
  new uncached.RelationshipTest(),
  new uncached.RoleConstraintsTest(),
  new uncached.RoleGroupsTest(),
  new uncached.RolePlayingAutomatonTest(),
  new uncached.RoleRestrictionsTest(),
  new uncached.RoleSortingTest(),
  new uncached.ThrowableInRoleMethodsTest(),
  new uncached.UnionTypesTest()
)
