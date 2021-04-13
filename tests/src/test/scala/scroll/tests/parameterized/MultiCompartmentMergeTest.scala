package scroll.tests.parameterized

import scroll.tests.mocks._

class MultiCompartmentMergeTest extends AbstractParameterizedSCROLLTest {

  test("union") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleB()
      }
      compA.compartmentRelations.union(compB)
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).play(new RoleC())
      compA.newPlayer(core).isPlaying[RoleC] shouldBe false
      compB.newPlayer(core).isPlaying[RoleC] shouldBe true
    }
  }

  test("combine") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleB()
      }
      compA.compartmentRelations.combine(compB)
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).play(new RoleC())
      compA.newPlayer(core).isPlaying[RoleC] shouldBe true
      compB.newPlayer(core).isPlaying[RoleC] shouldBe true
    }
  }

  test("partOf") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest(c, cc) {
        core play new RoleB()
      }
      compA.compartmentRelations.partOf(compB)
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe false
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
    }
  }

  test("notPartOf") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val core = new CoreA()
      class SomeRoleA
      class SomeRoleB
      val roleA = new SomeRoleA
      val roleB = new SomeRoleB
      val compA = new MultiCompartmentUnderTest(c, cc) {
        core play roleA
        core play roleB
      }
      val compB = new MultiCompartmentUnderTest(c, cc) {
        core play roleB
      }
      compA.compartmentRelations.notPartOf(compB)
      compA.newPlayer(core).roles() should contain only roleA
      compB.newPlayer(core).roles() should contain only roleB
    }
  }

}
