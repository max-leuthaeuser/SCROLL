package scroll.tests

import org.scalatest.Suites
import SCROLLTestConfig._

class SCROLLCachedTestSuite
  extends Suites(
    new RoleFeaturesTest { backend = CACHED },
    new DynamicExtensionsTest { backend = CACHED },
    new EqualityRoleTest { backend = CACHED },
    new RelationshipTest { backend = CACHED },
    new UnionTypesTest { backend = CACHED },
    new ECoreInstanceTest { backend = CACHED },
    new CROITest { backend = CACHED },
    new RoleConstraintsTest { backend = CACHED },
    new RolePlayingAutomatonTest { backend = CACHED },
    new RoleRestrictionsTest { backend = CACHED },
    new RoleGroupsTest { backend = CACHED })