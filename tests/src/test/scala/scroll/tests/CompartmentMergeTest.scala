package scroll.tests

import scroll.tests.mocks.{CoreA, SomeCompartment}

class CompartmentMergeTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for merging/splitting Compartments.")

  feature("Merging/splitting Compartments") {
    scenario("Testing union") {
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
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
      compB.newPlayer(core).play(new compB.RoleC())
      Then("Core should play all roles but only in one compartment")
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleC] shouldBe false
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleC] shouldBe true
    }

    scenario("Testing combine") {
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
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
      compB.newPlayer(core).play(new compB.RoleC())
      Then("Core should play all roles")
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleC] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleC] shouldBe true
    }

    scenario("Testing partOf") {
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
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe true
      compA.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleA] shouldBe false
      compB.newPlayer(core).isPlaying[SomeCompartment#RoleB] shouldBe true
    }

    scenario("Testing notPartOf") {
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
      compA.newPlayer(core).roles() should contain theSameElementsInOrderAs Seq(core, roleA)
      compA.newPlayer(core).roles() should not contain roleB
      compB.newPlayer(core).roles() should contain theSameElementsInOrderAs Seq(core, roleB)
      compB.newPlayer(core).roles() should not contain roleA
    }

  }

}
