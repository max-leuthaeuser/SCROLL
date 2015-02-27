import org.scalatest.Suites

class RoleSpecSuite
  extends Suites(
    new MinimalRoleSpec,
    new EqualityRoleSpec,
    new ExamplesRoleSpec,
    new UnionTypesRoleSpec)
