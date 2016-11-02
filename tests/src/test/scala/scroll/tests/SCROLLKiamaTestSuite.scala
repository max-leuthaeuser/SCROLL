package scroll.tests

import org.scalatest.Suites
import scroll.tests.SCROLLTestConfig._

class SCROLLKiamaTestSuite
  extends Suites(
    new RoleFeaturesTest { backend = KIAMA },
    new DynamicExtensionsTest { backend = KIAMA },
    new EqualityRoleTest { backend = KIAMA },
    new RelationshipTest { backend = KIAMA },
    new UnionTypesTest { backend = KIAMA },
    new ECoreInstanceTest { backend = KIAMA },
    new CROITest { backend = KIAMA },
    new RoleConstraintsTest { backend = KIAMA },
    new RolePlayingAutomatonTest { backend = KIAMA },
    new RoleRestrictionsTest { backend = KIAMA },
    new RoleGroupsTest { backend = KIAMA })