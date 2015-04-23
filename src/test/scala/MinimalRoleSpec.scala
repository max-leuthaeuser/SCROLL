import internal.DispatchQuery._
import internal.util.Log
import mocks.{CoreB, SomeCompartment, CoreA}
import org.scalatest._

class MinimalRoleSpec extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Dropping compartment and invoking methods") {
      Given("some player and role in a compartment")
      val someCore = new CoreA()
      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCore play someRole
        someCore play new RoleB()

        When("dropping the role")
        someCore drop someRole

        Then("the call must be invoked on the core object")
        someCore a()
        And("a role call should fail")
        a[RuntimeException] should be thrownBy {
          +someCore a()
        }
        And("binding to RoleB is left untouched of course")
        val resB: String = +someCore b()
        assert(resB == "b")
      }
    }

    scenario("Transferring a role") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("transferring the role")
        someCoreA transfer someRole to someCoreB

        Then("the result of the call to the role of player someCoreB should be correct")
        val res: Int = +someCoreB a()
        assert(res == 0)
        And("a call to the player the role was moved away from should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA a()
        }
      }
    }

    scenario("Handling applyDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method")
        val expected = 0
        val actual: Int = +someCoreA a()

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expected == actual)
        And("a call to the role with a method that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA c()
        }
      }
    }

    scenario("Handling applyDynamicNamed") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method with named params")
        val expected = someRole.b("some", param = "out")
        val actual: String = +someCoreA b("some", param = "out")

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expected == actual)
        And("a call to the role with a method that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          +someCoreA b("some", otherParam = "out")
        }
      }
    }

    scenario("Handling selectDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using selectDynamic to get the value of a role attribute")
        val expectedA = someRole.valueA
        val actualA: String = (+someCoreA).valueA
        val expectedB = someRole.valueB
        val actualB: Int = (+someCoreA).valueB

        Then("the result of the call to the role of player someCoreA should be correct")
        assert(expectedA == actualA)
        assert(expectedB == actualB)
        And("a call to the role with a value that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          (+someCoreA).valueC
        }
      }
    }

    scenario("Handling updateDynamic") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
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
        assert(expectedA == actualA)
        assert(expectedB == actualB)
        And("a call to the role with a value that does not exist should fail")
        a[RuntimeException] should be thrownBy {
          (+someCoreA).valueUnkown = "unknown"
        }
      }
    }

    scenario("Playing a role multiple times (same instance)") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
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
        assert(expected == actual1)
        assert(expected == actual2)
      }
    }

    scenario("Playing a role multiple times (different instances)") {
      Given("some players and 2 role instance of the same type in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
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
        assert(expected == actual1a || expected == actual1b)
        assert(expected == actual2)
      }
    }

    scenario("Playing a role multiple times (different instances, but using dispatch to select one)") {
      Given("some players and 2 role instance of the same type in a compartment")
      val someCoreA = new CoreA()

      new SomeCompartment {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        someRole1.valueB = 1
        someRole2.valueB = 2
        And("a play relationship")
        someCoreA play someRole1
        someCoreA play someRole2

        When("updating role attributes")

        implicit var dd = From(_.isInstanceOf[CoreA]).
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
        assert("valueC" == actual1)
        assert("updated" == actual2)
        assert("updated" == actual3)
      }
    }
  }

  scenario("Calling multi-argument method in roles") {
    Given("a player and a role instance in a compartment")
    val someCoreA = new CoreA()

    new SomeCompartment {
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
      assert(expected1 == actual1)
      assert(expected2 == actual2)
      assert(expected1 == actual3)
      assert(expected2 == actual4)
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

    new SomeCompartment {
      val someRole = new RoleE()

      And("a play relationship")
      someCoreA play someRole

      When("updating role attributes")

      var expectedInt: Int = 0
      var expectedDouble: Double = 0
      var expectedFloat: Float = 0
      var expectedLong: Long = 0
      var expectedShort: Short = 0
      var expectedByte: Byte = 0
      var expectedChar: Char = 'B'
      var expectedBoolean: Boolean = true

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
      assert(actualIntR == expectedInt)
      assert(actualDoubleR == expectedDouble)
      assert(actualFloatR == expectedFloat)
      assert(actualLongR == expectedLong)
      assert(actualShortR == expectedShort)
      assert(actualByteR == expectedByte)
      assert(actualCharR == expectedChar)
      assert(actualBooleanR == expectedBoolean)

      val actualIntP: Int = (+someCoreA).valueInt
      val actualDoubleP: Double = (+someCoreA).valueDouble
      val actualFloatP: Float = (+someCoreA).valueFloat
      val actualLongP: Long = (+someCoreA).valueLong
      val actualShortP: Short = (+someCoreA).valueShort
      val actualByteP: Byte = (+someCoreA).valueByte
      val actualCharP: Char = (+someCoreA).valueChar
      val actualBooleanP: Boolean = (+someCoreA).valueBoolean

      And("the player instance should be updated correctly.")
      assert(actualIntP == expectedInt)
      assert(actualDoubleP == expectedDouble)
      assert(actualFloatP == expectedFloat)
      assert(actualLongP == expectedLong)
      assert(actualShortP == expectedShort)
      assert(actualByteP == expectedByte)
      assert(actualCharP == expectedChar)
      assert(actualBooleanP == expectedBoolean)
    }
  }
}
