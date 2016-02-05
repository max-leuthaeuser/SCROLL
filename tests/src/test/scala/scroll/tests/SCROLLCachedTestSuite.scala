package scroll.tests

import org.scalatest.Suites

class SCROLLCachedTestSuite
  extends Suites(
    new RoleFeaturesTest { cached = true },
    new EqualityRoleTest { cached = true },
    new RelationshipTest { cached = true },
    new UnionTypesTest { cached = true },
    new ECoreInstanceTest { cached = true },
    new CROITest { cached = true },
    new RoleConstraintsTest { cached = true },
    new RolePlayingAutomatonTest { cached = true },
    new RoleRestrictionsTest { cached = true },
    new RoleGroupsTest { cached = true })