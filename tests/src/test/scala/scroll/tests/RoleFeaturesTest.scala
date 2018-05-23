package scroll.tests

import mocks.{CoreA, CoreB, SomeCompartment}
import org.scalatest._
import scroll.internal.support.DispatchQuery
import DispatchQuery._

class RoleFeaturesTest extends FeatureSpec with GivenWhenThen with Matchers {

  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Dropping role and invoking methods") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCore play someRole
        someCore play new RoleB()

        When("dropping the role")
        someCore drop someRole

        Then("the call must be invoked on the core object")
        someCore a()
        +someCore a()

        And("a role should be dropped correctly")
        (+someCore).isPlaying[RoleA] shouldBe false
        And("binding to RoleB is left untouched of course")
        (+someCore).isPlaying[RoleB] shouldBe true

        And("role method invocation should work.")
        val resB: String = +someCore b()
        resB shouldBe "b"
      }
    }

    scenario("Transferring a role") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("transferring the role")
        someCoreA transfer someRole to someCoreB

        Then("the result of the call to the role of player someCoreB should be correct")
        val res: Int = +someCoreB a()
        res shouldBe 0
        And("the role should be transferred correctly.")
        (+someCoreA).isPlaying[RoleA] shouldBe false
        (+someCoreB).isPlaying[RoleA] shouldBe true
      }
    }

    scenario("Role playing and testing isPlaying") {
      Given("some players and roles in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new SomeCompartment() {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        And("a play relationship")
        someCoreA play someRoleA

        When("calling is Playing")
        Then("it should return false if the role is not played")
        someCoreA.isPlaying[RoleB] shouldBe false
        And("it should return false is the player is not in the role playing graph yet")
        someCoreB.isPlaying[RoleA] shouldBe false
        someCoreB.isPlaying[RoleB] shouldBe false
        And("it should return true if the role is actually played")
        someCoreA.isPlaying[RoleA] shouldBe true
      }
    }

    scenario("Handling applyDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method")
        val expected = 0
        val actual: Int = +someCoreA a()

        Then("the result of the call to the role of player someCoreA should be correct")
        expected shouldBe actual
        And("a call to the role with a method that does not exist should fail")
        val r = +someCoreA c()
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }

    scenario("Handling applyDynamicNamed") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method with named params")
        val expected = someRole.b("some", param = "out")
        val actual: String = +someCoreA b("some", param = "out")

        Then("the result of the call to the role of player someCoreA should be correct")
        expected shouldBe actual
      }
    }

    scenario("Handling selectDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using selectDynamic to get the value of a role attribute")
        val expectedA = someRole.valueA
        val actualA: String = (+someCoreA).valueA
        val expectedB = someRole.valueB
        val actualB: Int = (+someCoreA).valueB

        Then("the result of the call to the role of player someCoreA should be correct")
        expectedA shouldBe actualA
        expectedB shouldBe actualB
        And("a call to the role with a value that does not exist should fail")
        val r = (+someCoreA).valueD
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }

    scenario("Handling updateDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using updateDynamic to get the value of a role attribute")
        val expectedA = "newValue"
        (+someCoreA).valueA = expectedA
        val actualA: String = (+someCoreA).valueA

        val expectedB = -1
        (+someCoreA).valueB = expectedB
        val actualB: Int = (+someCoreA).valueB

        Then("the result of the call to the role of player someCoreA should be correct")
        expectedA shouldBe actualA
        expectedB shouldBe actualB
      }
    }
  }

  scenario("Playing a role multiple times (same instance)") {
    Given("some players and role in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRole = new RoleA()
      And("a play relationship")
      someCoreA play someRole
      someCoreA play someRole

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)

      val actual1: String = someRole.valueC
      val actual2: String = (+someCoreA).valueC

      Then("the role and player instance should be updated correctly.")
      expected shouldBe actual1
      expected shouldBe actual2
    }
  }

  scenario("Playing a role multiple times (different instances) from one player") {
    Given("some players and 2 role instance of the same type in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      And("a play relationship")
      someCoreA play someRole1
      someCoreA play someRole2

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)

      val actual1a: String = someRole1.valueC
      val actual1b: String = someRole2.valueC
      val actual2: String = (+someCoreA).valueC

      Then("one role and the player instance should be updated correctly.")
      (expected == actual1a || expected == actual1b) shouldBe true
      expected shouldBe actual2
    }
  }

  scenario("Playing a role multiple times (different instances, but using dispatch to select one)") {
    Given("some players and 2 role instance of the same type in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      someRole1.valueB = 1
      someRole2.valueB = 2
      And("a play relationship")
      someCoreA play someRole1
      someCoreA play someRole2

      When("updating role attributes")

      implicit val dd: DispatchQuery = From(_.isInstanceOf[CoreA]).
        To(_.isInstanceOf[RoleA]).
        Through(anything).
        Bypassing({
          case r: RoleA => 1 == r.valueB // so we ignore someRole1 here while dispatching the call to update
          case _ => false
        })

      (+someCoreA).update("updated")

      val actual1: String = someRole1.valueC
      val actual2: String = someRole2.valueC
      val actual3: String = (+someCoreA).valueC

      Then("one role and the player instance should be updated correctly.")
      "valueC" shouldBe actual1
      "updated" shouldBe actual2
      "updated" shouldBe actual3
    }
  }

  scenario("Calling multi-argument method in roles") {
    Given("a player and a role instance in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRole = new RoleD()

      And("a play relationship")
      someCoreA play someRole

      When("updating role attributes")

      val expected1 = "updated"
      val expected2 = 1

      (+someCoreA).update(expected1, expected2)

      val actual1 = someRole.valueA
      val actual2 = someRole.valueB
      val actual3: String = (+someCoreA).valueA
      val actual4: Int = (+someCoreA).valueB

      Then("the role and the player instance should be updated correctly.")
      expected1 shouldBe actual1
      expected2 shouldBe actual2
      expected1 shouldBe actual3
      expected2 shouldBe actual4
    }
  }

  /**
    * test case for primitive types:
    * Int
    * Double
    * Float
    * Long
    * Short
    * Byte
    * Char
    * boolean
    */
  scenario("Calling method on a role with different primitive types") {
    Given("a player and a role instance in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRole = new RoleE()

      And("a play relationship")
      someCoreA play someRole

      When("updating role attributes")

      val expectedInt: Int = 0
      val expectedDouble: Double = 0
      val expectedFloat: Float = 0
      val expectedLong: Long = 0
      val expectedShort: Short = 0
      val expectedByte: Byte = 0
      val expectedChar: Char = 'B'
      val expectedBoolean: Boolean = true

      (+someCoreA).updateInt(expectedInt)
      (+someCoreA).updateDouble(expectedDouble)
      (+someCoreA).updateFloat(expectedFloat)
      (+someCoreA).updateLong(expectedLong)
      (+someCoreA).updateShort(expectedShort)
      (+someCoreA).updateByte(expectedByte)
      (+someCoreA).updateChar(expectedChar)
      (+someCoreA).updateBoolean(expectedBoolean)

      val actualIntR = someRole.valueInt
      val actualDoubleR = someRole.valueDouble
      val actualFloatR = someRole.valueFloat
      val actualLongR = someRole.valueLong
      val actualShortR = someRole.valueShort
      val actualByteR = someRole.valueByte
      val actualCharR = someRole.valueChar
      val actualBooleanR = someRole.valueBoolean

      Then("the role instance should be updated correctly.")
      actualIntR shouldBe expectedInt
      actualDoubleR shouldBe expectedDouble
      actualFloatR shouldBe expectedFloat
      actualLongR shouldBe expectedLong
      actualShortR shouldBe expectedShort
      actualByteR shouldBe expectedByte
      actualCharR shouldBe expectedChar
      actualBooleanR shouldBe expectedBoolean

      val actualIntP: Int = (+someCoreA).valueInt
      val actualDoubleP: Double = (+someCoreA).valueDouble
      val actualFloatP: Float = (+someCoreA).valueFloat
      val actualLongP: Long = (+someCoreA).valueLong
      val actualShortP: Short = (+someCoreA).valueShort
      val actualByteP: Byte = (+someCoreA).valueByte
      val actualCharP: Char = (+someCoreA).valueChar
      val actualBooleanP: Boolean = (+someCoreA).valueBoolean

      And("the player instance should be updated correctly.")
      actualIntP shouldBe expectedInt
      actualDoubleP shouldBe expectedDouble
      actualFloatP shouldBe expectedFloat
      actualLongP shouldBe expectedLong
      actualShortP shouldBe expectedShort
      actualByteP shouldBe expectedByte
      actualCharP shouldBe expectedChar
      actualBooleanP shouldBe expectedBoolean
    }
  }

  scenario("Playing a role multiple times (same instance) from different players") {
    Given("some players and role in a compartment")
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()

    new SomeCompartment() {
      implicit var dd: DispatchQuery = DispatchQuery.empty

      val someRole = new RoleA()
      And("a play relationship")
      someCoreA play someRole
      someCoreB play someRole

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)
      (+someCoreB).update(expected)

      val actual1: String = someRole.valueC
      val actual2: String = (+someCoreA).valueC
      val actual3: String = (+someCoreB).valueC

      Then("the role and player instance should be updated correctly.")
      expected shouldBe actual1
      expected shouldBe actual2
      expected shouldBe actual3

      When("getting the player of RoleA without an explicit dispatch description")
      val player = someRole.player match {
        case Left(_) => fail("Player should be defined here!")
        case Right(p) => p
      }
      Then("it should be one of the player this role actually plays.")
      (player == someCoreA || player == someCoreB) shouldBe true

      When("getting the player of RoleA with an explicit dispatch description")
      dd = From(anything).
        To(c => c.isInstanceOf[CoreA] || c.isInstanceOf[CoreB]).
        Through(anything).
        Bypassing(_.isInstanceOf[CoreB])
      val player2 = someRole.player match {
        case Left(_) => fail("Player should be defined here!")
        case Right(p) => p
      }
      Then("it should be the correct player.")
      player2 shouldBe someCoreA
    }
  }

  scenario("Cyclic role-playing relationship") {
    Given("a player and some roles in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRoleA = new RoleA()
      val someRoleB = new RoleB()
      val someRoleC = new RoleC()
      And("some play relationships")
      When("creating a cycle")
      someCoreA play someRoleA
      someRoleA play someRoleB
      someRoleB play someRoleC
      Then("a runtime exception should be thrown")
      a[RuntimeException] should be thrownBy {
        someRoleC play someRoleA
      }
    }
  }

  scenario("Compartment plays a role that is part of themselves") {
    Given("a compartment and a role in it")

    class ACompartment extends SomeCompartment {

      class ARole

    }

    And("an new instance of that compartment")
    new ACompartment {
      When("defining a play relationship")
      this play new ARole
      Then("That compartment should be able to play that role")
      this.isPlaying[ARole] shouldBe true
    }
  }

  scenario("Deep roles") {
    Given("a player and some roles in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRoleA = new RoleA()
      val someRoleB = new RoleB()
      val someRoleC = new RoleC()
      val someRoleD = new RoleD()
      val someRoleE = new RoleE()
      val expectedVal = 10
      And("some play relationships")
      When("using deep roles")
      someCoreA play someRoleA
      someRoleA play someRoleB
      someRoleB play someRoleC
      someRoleC play someRoleD
      someRoleD play someRoleE
      And("setting a value of some attached role")
      (+someCoreA).valueInt = expectedVal
      val actualVal1: Int = (+someCoreA).valueInt
      val actualVal2: Int = (+someRoleB).valueInt
      val actualVal3: Int = (+someRoleC).valueInt
      val actualVal4: Int = (+someRoleD).valueInt
      val actualVal5: Int = (+someRoleE).valueInt
      val actualVal6: Int = someRoleE.valueInt
      Then("the value should be set correctly")
      actualVal1 shouldBe expectedVal
      actualVal2 shouldBe expectedVal
      actualVal3 shouldBe expectedVal
      actualVal4 shouldBe expectedVal
      actualVal5 shouldBe expectedVal
      actualVal6 shouldBe expectedVal
    }
  }

  scenario("Handling null arguments for applyDynamic") {
    Given("a player and a role in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment() {
      val someRoleA = new RoleA()
      val expected: String = "valueC"
      And("a play relationship")
      val p = someCoreA play someRoleA
      var actual: String = p.valueC
      actual shouldBe expected
      When("passing null")
      Then("no exception should be thrown")
      p.update(null)
      And("the field should be set correctly")
      actual = p.valueC
      actual shouldBe null
    }
  }
}