package scroll.tests

import mocks.{CoreA, SomeCompartment}
import org.scalatest._

class RelationshipTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for the relationship concept.")

  feature("Relationship specification and querying") {
    scenario("Specifying a relationship") {
      Given("A compartment, a player and attached roles")

      val p = new CoreA
      new SomeCompartment() {
        val rA = new RoleA
        val rB = new RoleB
        val rC = new RoleC
        p play rA play rB

        When("specifying a 1-1 relationship")
        val rel1 = Relationship("rel1").from[RoleA](1).to[RoleB](1)
        Then("the given multiplicities and queries should be correct")
        rel1.left() shouldBe Seq(rA)
        rel1.right() shouldBe Seq(rB)
        And("querying for a role that is not played should throw an error")
        val rel2 = Relationship("rel2").from[RoleA](1).to[RoleC](1)
        rel2.left() shouldBe Seq(rA)
        a[AssertionError] should be thrownBy {
          rel2.right()
        }

        When("specifying a 1-* relationship")

        import scroll.internal.util.Many._

        val rel3 = Relationship("rel3").from[RoleA](1).to[RoleB](*)
        Then("the given multiplicities and queries should be correct")
        rel3.left() shouldBe Seq(rA)
        rel3.right() shouldBe Seq(rB)
        val rB2 = new RoleB
        p play rB2
        rel3.right() shouldBe Seq(rB, rB2)
        val rB3 = new RoleB
        p play rB3
        rel3.right() shouldBe Seq(rB, rB2, rB3)
      }
    }
  }
}
