import mocks.{CoreA, SomeCompartment}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

class RoleConstraintsTest extends FeatureSpec with GivenWhenThen with Matchers {

  val SUCCESS = 'left
  val FAILURE = 'right

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
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player drop roleB
        } should be(FAILURE)

        RoleConstraintsChecked {
          player play roleB
        } should be(SUCCESS)

        RoleImplication[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleC
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player drop roleB
        } should be(FAILURE)

        RoleConstraintsChecked {
          player drop roleC
        } should be(FAILURE)

        RoleConstraintsChecked {
          player play roleC play roleB
        } should be(SUCCESS)
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
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player drop roleA
          player play roleB
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player play roleA
        } should be(FAILURE)

        RoleProhibition[RoleB, RoleC]()
        RoleConstraintsChecked {
          player drop roleA
          player drop roleB
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        } should be(FAILURE)

        RoleConstraintsChecked {
          player drop roleB
        } should be(SUCCESS)
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
        RoleConstraintsChecked {
          player play roleA
        } should be(FAILURE)

        RoleConstraintsChecked {
          player play roleB
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player drop roleA
        } should be(FAILURE)

        RoleConstraintsChecked {
          player drop roleB
        } should be(SUCCESS)

        RoleEquivalence[RoleB, RoleC]()
        RoleConstraintsChecked {
          player play roleA
          player play roleB
          player play roleC
        } should be(SUCCESS)

        RoleConstraintsChecked {
          player drop roleB
        } should be(FAILURE)
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
        RoleConstraintsChecked {
          player play roleA
          player play roleB
        } should be(FAILURE)
      }
    }
  }
}
