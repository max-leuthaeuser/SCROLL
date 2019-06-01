package scroll.tests.cached

import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks._

class MultiCompartmentMergeTest extends AbstractSCROLLTest {
  info(s"Test spec for merging/splitting MultiCompartments with cache = '$cached'.")

  Feature("Merging/splitting MultiCompartments") {
    Scenario("Testing union") {
      Given("Two MultiCompartments and some cores/roles")
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling union on those MultiCompartments")
      compA.union(compB)
      Then("Core should play all roles")
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).play(new RoleC())
      Then("Core should play all roles but only in one MultiCompartment")
      compA.newPlayer(core).isPlaying[RoleC] shouldBe false
      compB.newPlayer(core).isPlaying[RoleC] shouldBe true
    }

    Scenario("Testing combine") {
      Given("Two MultiCompartments and some cores/roles")
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling combine on those MultiCompartments")
      compA.combine(compB)
      Then("Core should play all roles")
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).play(new RoleC())
      Then("Core should play all roles")
      compA.newPlayer(core).isPlaying[RoleC] shouldBe true
      compB.newPlayer(core).isPlaying[RoleC] shouldBe true
    }

    Scenario("Testing partOf") {
      Given("Two MultiCompartments and some cores/roles")
      val core = new CoreA()
      val compA = new MultiCompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new MultiCompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling partOf on those MultiCompartments")
      compA.partOf(compB)
      Then("Core should play the correct roles")
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe false
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
    }

    Scenario("Testing notPartOf") {
      Given("Two MultiCompartments and some cores/roles")
      val core = new CoreA()
      class SomeRoleA
      class SomeRoleB
      val roleA = new SomeRoleA
      val roleB = new SomeRoleB
      val compA = new MultiCompartmentUnderTest {
        core play roleA
        core play roleB
      }
      val compB = new MultiCompartmentUnderTest {
        core play roleB
      }
      When("Calling notPartOf on those MultiCompartments")
      compA.notPartOf(compB)
      Then("Core should play the correct roles")
      compA.newPlayer(core).roles() should contain only roleA
      compB.newPlayer(core).roles() should contain only roleB
    }

  }

}
