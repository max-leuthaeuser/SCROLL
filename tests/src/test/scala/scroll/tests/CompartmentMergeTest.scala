package scroll.tests

import mocks._

class CompartmentMergeTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info(s"Test spec for merging/splitting Compartments with cache = '$cached'.")

  Feature("Merging/splitting Compartments") {
    Scenario("Testing union") {
      Given("Two Compartments and some cores/roles")
      val core = new CoreA()
      val compA = new CompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new CompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling union on those Compartments")
      compA.union(compB)
      Then("Core should play all roles")
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).play(new RoleC())
      Then("Core should play all roles but only in one Compartment")
      compA.newPlayer(core).isPlaying[RoleC] shouldBe false
      compB.newPlayer(core).isPlaying[RoleC] shouldBe true
    }

    Scenario("Testing combine") {
      Given("Two Compartments and some cores/roles")
      val core = new CoreA()
      val compA = new CompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new CompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling combine on those Compartments")
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
      Given("Two Compartments and some cores/roles")
      val core = new CoreA()
      val compA = new CompartmentUnderTest {
        core play new RoleA()
      }
      val compB = new CompartmentUnderTest {
        core play new RoleB()
      }
      When("Calling partOf on those Compartments")
      compA.partOf(compB)
      Then("Core should play the correct roles")
      compA.newPlayer(core).isPlaying[RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[RoleA] shouldBe false
      compB.newPlayer(core).isPlaying[RoleB] shouldBe true
    }

    Scenario("Testing notPartOf") {
      Given("Two Compartments and some cores/roles")
      val core = new CoreA()
      class SomeRoleA
      class SomeRoleB
      val roleA = new SomeRoleA
      val roleB = new SomeRoleB
      val compA = new CompartmentUnderTest {
        core play roleA
        core play roleB
      }
      val compB = new CompartmentUnderTest {
        core play roleB
      }
      When("Calling notPartOf on those Compartments")
      compA.notPartOf(compB)
      Then("Core should play the correct roles")
      compA.newPlayer(core).roles() should contain only roleA
      compB.newPlayer(core).roles() should contain only roleB
    }

  }

}
