import mocks.{CoreA, SomeCompartment}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class RoleConstraintsTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for role constraints.")

  feature("Role implication") {
    scenario("Role implication constraint") {
      new SomeCompartment {
        Given("A compartment, a player and two roles")
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
        } should be('left)

        RoleConstraintsChecked {
          player drop roleB
        } should be('right)

        RoleConstraintsChecked {
          player play roleB
        } should be('left)

        RoleImplication[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleC
        } should be('left)

        RoleConstraintsChecked {
          player drop roleB
        } should be('right)

        RoleConstraintsChecked {
          player drop roleC
        } should be('right)

        RoleConstraintsChecked {
          player play roleC play roleB
        } should be('left)
      }
    }
  }

  feature("Role prohibition") {
    scenario("Role prohibition constraint") {
      new SomeCompartment {
        Given("A compartment, a player and two roles")
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
        } should be('left)

        RoleConstraintsChecked {
          player drop roleA
          player play roleB
        } should be('left)

        RoleConstraintsChecked {
          player play roleA
        } should be('right)

        RoleProhibition[RoleB, RoleC]()
        RoleConstraintsChecked {
          player drop roleA
          player drop roleB
        } should be('left)

        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        } should be('right)

        RoleConstraintsChecked {
          player drop roleB
        } should be('left)
      }
    }
  }

  // TODO add tests for role equivalence
}
