import org.scalatest.Suites

class RoleSpecSuite
  extends Suites(
    new MinimalRoleSpec,
    new EqualityRoleSpec,
    new ExamplesRoleSpec,
    new RelationshipSpec,
    new UnionTypesRoleSpec,
    new FormalCROMSpec,
    new FormalCROMExampleSpec,
    new ECoreInstanceTest,
    new CROITest,
    new RoleConstraintsTest,
    new RolePlayingAutomatonTest,
    new RoleRestrictionsTest)
