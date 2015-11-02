package scroll.tests

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.tests.mocks.SomeCompartment

class RoleGroupsTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role groups.")

  feature("Role groups") {
    scenario("Calculating seq of types") {
      new SomeCompartment {
        Given("A compartment and some role groups")

        When("adding some role groups")
        val rg1 = RoleGroup("rg1").containing[RoleA].from(1).to(1)
        val rg2 = RoleGroup("rg2").containing[RoleB].from(1).to(1)
        val comb = RoleGroup("comp").containing(rg1, rg2).from(1).to(1)

        Then("the resulting types seq should be correct")
        comb.getTypes shouldBe Seq("RoleA", "RoleB")
      }
    }
    scenario("Added role groups with the same name") {
      new SomeCompartment {
        Given("A compartment and some role groups")

        When("adding some role groups")
        Then("it should fail adding them twice with the same name")

        val rg1 = RoleGroup("rg1").containing[RoleA].from(1).to(1)
        a[RuntimeException] should be thrownBy {
          RoleGroup("rg1").containing[RoleA].from(1).to(1)
        }
      }
    }
  }
}
