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
    new UnionTypesTest,
    new FormalCROMTest,
    new FormalCROMExampleTest,
    new ECoreInstanceTest,
    new CROITest,
    new RoleConstraintsTest,
    new RolePlayingAutomatonTest,
    new RoleRestrictionsTest,
    new RoleGroupsTest)