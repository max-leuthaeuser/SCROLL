package scroll.tests

import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.tests.mocks.{CoreA, SomeCompartment}

class RoleGroupsTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role groups.")

  feature("Role groups") {
    scenario("Validating role group cardinality") {
      val acc1 = new CoreA()
      val acc2 = new CoreA()
      new SomeCompartment {
        Given("A compartment and a role group")

        class Source

        class Target

        val source = new Source
        val target = new Target

        When("adding the role group")
        val transaction = RoleGroup("Transaction").containing[Source, Target](1, 1)(2, 2)

        Then("the validation should be correct")
        RoleGroupsChecked {
          acc1 play source
          acc2 play target
        }

        And("an exception should be thrown if the validation fails!")
        a[RuntimeException] should be thrownBy {
          RoleGroupsChecked {
            acc2 drop target
          }
        }

        And("an exception should be thrown if the validation fails!")
        a[RuntimeException] should be thrownBy {
          RoleGroupsChecked {
            acc1 play target
          }
        }
      }
    }
  }
}
