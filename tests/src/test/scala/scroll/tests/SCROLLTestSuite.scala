package scroll.tests

import org.scalatest.Suites

class SCROLLTestSuite
  extends Suites(
    new RoleFeaturesTest,
    new RoleSortingTest,
    new DynamicExtensionsTest,
    new EqualityRoleTest,
    new ExamplesTest,
    new RelationshipTest,
    new RoleConstraintsTest,
    new RoleRestrictionsTest,
    new RoleGroupsTest,
    new MultiRoleFeaturesTest,
    new FacetsTests,
    new RecursiveBaseCallsWithClassesTest,
    new RecursiveBaseCallsWithCaseClassesTest,
    new ThrowableInRoleMethodsTest)