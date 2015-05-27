import mocks.{SomeCompartment, CoreA}
import org.scalatest._

class RelationshipSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for the relationship concept.")

  feature("Relationship specification and querying") {
    scenario("Specifying a relationship") {
      Given("A compartment, a player and attached roles")

      val p = new CoreA
      new SomeCompartment {
        val rA = new RoleA
        val rB = new RoleB
        p play rA play rB

        When("specifying a 1-1 relationship")
        val rel = Relationship("rel").from[RoleA](1).to[RoleB](1)
        Then("the given multiplicities and queries should be correct")
        rel.left() shouldBe Seq(rA)
        rel.right() shouldBe Seq(rB)
      }
    }
  }
}
