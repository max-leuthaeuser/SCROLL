package scroll.tests.cached

import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks._

class RoleConstraintsTest extends AbstractSCROLLTest {
  info(s"Test spec for role constraints with cache = '$cached'.")

  Feature("Role implication") {
    Scenario("Role implication constraint") {
      new CompartmentUnderTest() {
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

  Feature("Role prohibition") {
    Scenario("Role prohibition constraint") {
      new CompartmentUnderTest() {
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

  Feature("Role equivalence") {
    Scenario("Role equivalence constraint") {
      new CompartmentUnderTest() {
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

  Feature("Mixed constraints") {
    Scenario("Role implication and prohibition constraint") {
      new CompartmentUnderTest() {
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
