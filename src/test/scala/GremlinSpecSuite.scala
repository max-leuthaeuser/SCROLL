import org.scalatest.Suites

class GremlinSpecSuite extends Suites(
  new GremlinScalaSpec,
  new ShortestPathGramlinScalaSpec)