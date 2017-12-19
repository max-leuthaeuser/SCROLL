package scroll.tests

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.tests.mocks.{CoreA, CoreB, SomeCompartment}

class FacetsTests extends FeatureSpec with GivenWhenThen with Matchers {

  object TestFacet extends Enumeration {
    type Color = Value
    val Red, Blue, Green = Value
  }

  import TestFacet._

  info("Test spec for facets.")
  info("Things like filtering for specific facets are tested.")

  feature("Facets spec and attachment") {
    scenario("Adding facets") {
      Given("some player and a facet in a compartment")
      val someCore = new CoreA()
      new SomeCompartment() {
        When("a facet is attached to the player")
        val player = someCore <+> Red
        Then("the facet should be found")
        player.hasFacet(Red) shouldBe true
      }
    }

    scenario("Removing facets") {
      Given("some player and a facet in a compartment")
      val someCore = new CoreA()
      new SomeCompartment() {
        When("a facet is attached to the player")
        val player = someCore <+> Red
        And("is removed later on")
        player.drop(Red)
        Then("the facet should be not be found any longer")
        player.hasFacet(Red) shouldBe false
      }
    }

    scenario("Transferring facets") {
      Given("some player and a facet in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new SomeCompartment() {
        When("a facet is attached to a player")
        val playerA = someCoreA <+> Red
        And("is transferred later on")
        val playerB = +someCoreB
        someCoreA transfer Red to someCoreB
        Then("the facet should be not be found any longer on the first player")
        playerA.hasFacet(Red) shouldBe false
        And("the second player should have the facet now")
        playerB.hasFacet(Red) shouldBe true
      }
    }

    scenario("Filtering for facets") {
      Given("some player and facets in a compartment")
      val someCoreA1 = new CoreA()
      val someCoreA2 = new CoreA()
      val someCoreA3 = new CoreA()
      val someCoreA4 = new CoreA()
      val someCoreA5 = new CoreA()
      val someCoreA6 = new CoreA()

      new SomeCompartment() {
        When("some facets are attached")
        someCoreA1 <+> Red
        someCoreA2 <+> Red
        someCoreA3 <+> Red
        someCoreA4 <+> Blue
        someCoreA5 <+> Blue
        someCoreA6 <+> Blue
        And("we filter for a specific facet")
        Then("only those object having the correct facet should be returned")
        all { c: CoreA => c.hasFacet(Red) } should contain only(someCoreA1, someCoreA2, someCoreA3)
        all { c: CoreA => c.hasFacet(Blue) } should contain only(someCoreA4, someCoreA5, someCoreA6)
        all { c: CoreA => c.hasFacet(Green) } shouldBe empty
      }
    }
  }

}
