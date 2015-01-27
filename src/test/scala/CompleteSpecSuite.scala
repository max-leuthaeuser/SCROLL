import org.scalatest.Suites

class CompleteSpecSuite
  extends Suites(
    new MinimalRoleSpec,
    new EqualityRoleSpec,
    new ExamplesRoleSpec,
    new GremlinScalaSpec,
    new ShortestPathGramlinScalaSpec)
