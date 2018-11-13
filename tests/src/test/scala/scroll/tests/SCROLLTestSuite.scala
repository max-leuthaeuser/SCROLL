package scroll.tests

import org.scalatest.Suites

object SCROLLTestSuite {
  val suites: Seq[AbstractSCROLLTest] = Seq(true, false).flatMap(c => {
    Seq(
      new RoleFeaturesTest(cached = c),
      new RoleSortingTest(cached = c),
      new DynamicExtensionsTest(cached = c),
      new EqualityRoleTest(cached = c),
      new RelationshipTest(cached = c),
      new UnionTypesTest(cached = c),
      new FormalCROMTest(cached = c),
      new FormalCROMExampleTest(cached = c),
      new ECoreInstanceTest(cached = c),
      new CROITest(cached = c),
      new RoleConstraintsTest(cached = c),
      new RolePlayingAutomatonTest(cached = c),
      new RoleRestrictionsTest(cached = c),
      new RoleGroupsTest(cached = c),
      new MultiRoleFeaturesTest(cached = c),
      new FacetsTest(cached = c),
      new RecursiveBaseCallsWithClassesTest(cached = c),
      new RecursiveBaseCallsWithCaseClassesTest(cached = c),
      new ThrowableInRoleMethodsTest(cached = c),
      new MultiCompartmentTest(cached = c),
      new CompartmentMergeTest(cached = c))
  }) :+ new ExamplesTest() :+ new SCROLLErrorsTest()
}

class SCROLLTestSuite extends Suites(SCROLLTestSuite.suites: _*)
