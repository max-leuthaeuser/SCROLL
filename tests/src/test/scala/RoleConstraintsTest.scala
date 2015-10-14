import mocks.{CoreA, SomeCompartment}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class RoleConstraintsTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role constraints.")

  feature("Role implication") {
    scenario("Role implication constraint") {
      new SomeCompartment {
        Given("A compartment, a player and some roles")
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        And("an role implication constraint")
        RoleImplication[RoleA, RoleB]()
        When("checking the constraints")
        Then("they should hold")
        RoleConstraintsChecked {
          player play roleA play roleB
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }

        RoleConstraintsChecked {
          player play roleB
        }

        RoleImplication[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleC
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleC
          }
        }

        RoleConstraintsChecked {
          player play roleC play roleB
        }
      }
    }
  }

  feature("Role prohibition") {
    scenario("Role prohibition constraint") {
      new SomeCompartment {
        Given("A compartment, a player and some roles")
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        And("an role prohibition constraint")
        RoleProhibition[RoleA, RoleB]()
        When("checking the constraints")
        Then("they should hold")
        RoleConstraintsChecked {
          player play roleA
        }

        RoleConstraintsChecked {
          player drop roleA
          player play roleB
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        }

        RoleProhibition[RoleB, RoleC]()
        RoleConstraintsChecked {
          player drop roleA
          player drop roleB
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
            player play roleC
          }
        }

        RoleConstraintsChecked {
          player drop roleB
        }
      }
    }
  }

  feature("Role equivalence") {
    scenario("Role equivalence constraint") {
      new SomeCompartment {
        Given("A compartment, a player and some roles")
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        val roleC = new RoleC()
        And("an role equivalence constraint")
        RoleEquivalence[RoleA, RoleB]()
        When("checking the constraints")
        Then("they should hold")
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
          }
        }

        RoleConstraintsChecked {
          player play roleB
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleA
          }
        }

        RoleConstraintsChecked {
          player drop roleB
        }

        RoleEquivalence[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        }

        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player drop roleB
          }
        }
      }
    }
  }

  feature("Mixed constraints") {
    scenario("Role implication and prohibition constraint") {
      new SomeCompartment {
        Given("A compartment, a player and some roles")
        val player = new CoreA()
        val roleA = new RoleA()
        val roleB = new RoleB()
        And("an implication and prohibition constraint")
        RoleImplication[RoleA, RoleB]()
        RoleProhibition[RoleA, RoleB]()

        When("checking the constraints")
        Then("they should hold")
        a[RuntimeException] should be thrownBy {
          RoleConstraintsChecked {
            player play roleA
            player play roleB
          }
        }
      }
    }
  }
}
