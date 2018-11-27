package scroll.tests

import mocks._
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._

class MultiCompartmentRoleFeaturesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {

  info("Test spec for an excerpt of the role concept.")
  info("Things like role playing and method invocation are tested.")

  feature("Role playing") {
    scenario("Dropping role and invoking methods") {
      Given("some player and role in a multi compartment")
      val someCore = new CoreA()
      new MultiCompartmentUnderTest() {
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
        +someCore b() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
          case Left(error) => fail(error.toString)
        }
      }
    }

    scenario("Transferring a role") {
      Given("some players and role in a multi compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new MultiCompartmentUnderTest() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("transferring the role")
        someCoreA transfer someRole to someCoreB

        Then("the result of the call to the role of player someCoreB should be correct")
        +someCoreB a() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(0))
          case Left(error) => fail(error.toString)
        }
        And("the role should be transferred correctly.")
        (+someCoreA).isPlaying[RoleA] shouldBe false
        (+someCoreB).isPlaying[RoleA] shouldBe true
      }
    }

    scenario("Role playing and testing isPlaying") {
      Given("some players and roles in a multi compartment")
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()

      new MultiCompartmentUnderTest() {
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
      Given("some players and role in a multi compartment")
      val someCoreA = new CoreA()

      new MultiCompartmentUnderTest() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method")
        Then("the result of the call to the role of player someCoreA should be correct")
        val expected = 0
        +someCoreA a[Int]() match {
          case Right(returnValue) => returnValue.head shouldBe Right(expected)
          case Left(error) => fail(error.toString)
        }
        And("a call to the role with a method that does not exist should fail")
        val r = +someCoreA c()
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }

    scenario("Handling applyDynamicNamed") {
      Given("some players and role in a multi compartment")
      val someCoreA = new CoreA()

      new MultiCompartmentUnderTest() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("calling a dynamic method with named params")
        val expected = someRole.b("some", param = "out")
        Then("the result of the call to the role of player someCoreA should be correct")
        +someCoreA b("some", param = "out") match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
      }
    }

    scenario("Handling selectDynamic") {
      Given("some players and role in a multi compartment")
      val someCoreA = new CoreA()

      new MultiCompartmentUnderTest() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using selectDynamic to get the value of a role attribute")
        Then("the result of the call to the role of player someCoreA should be correct")

        val expectedA = someRole.valueA
        (+someCoreA).valueA[String] match {
          case Right(returnValue) => returnValue.head shouldBe Right(expectedA)
          case Left(error) => fail(error.toString)
        }

        val expectedB = someRole.valueB
        (+someCoreA).valueB[Int] match {
          case Right(returnValue) => returnValue.head shouldBe Right(expectedB)
          case Left(error) => fail(error.toString)
        }

        And("a call to the role with a value that does not exist should fail")
        val r = (+someCoreA).valueD
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }

    scenario("Handling updateDynamic") {
      Given("some players and role in a multi compartment")
      val someCoreA = new CoreA()

      new MultiCompartmentUnderTest() {
        val someRole = new RoleA()
        And("a play relationship")
        someCoreA play someRole

        When("using updateDynamic to get the value of a role attribute")
        Then("the result of the call to the role of player someCoreA should be correct")

        val expectedA = "newValue"
        (+someCoreA).valueA = expectedA
        (+someCoreA).valueA[String] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedA))
          case Left(error) => fail(error.toString)
        }

        val expectedB = -1
        (+someCoreA).valueB = expectedB
        (+someCoreA).valueB[Int] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedB))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  scenario("Playing a role multiple times (same instance)") {
    Given("some players and role in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
      val someRole = new RoleA()
      And("a play relationship")
      someCoreA play someRole
      someCoreA play someRole

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)

      Then("the role and player instance should be updated correctly.")
      val actual1: String = someRole.valueC
      expected shouldBe actual1
      (+someCoreA).valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Playing a role multiple times (different instances) from one player") {
    Given("some players and 2 role instance of the same type in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      And("a play relationship")
      someCoreA play someRole1
      someCoreA play someRole2

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)

      Then("both roles and the player instance should be updated correctly.")
      val actual1a: String = someRole1.valueC
      val actual1b: String = someRole2.valueC
      (expected == actual1a || expected == actual1b) shouldBe true
      (+someCoreA).valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected), Right(expected))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Playing a role multiple times (different instances, but using dispatch to select one)") {
    Given("some players and 2 role instance of the same type in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      someRole1.valueB = 1
      someRole2.valueB = 2
      And("a play relationship")
      someCoreA play someRole1
      someCoreA play someRole2

      When("updating role attributes")

      implicit val dd = From(_.isInstanceOf[CoreA]).
        To(_.isInstanceOf[RoleA]).
        Through(anything).
        Bypassing({
          case r: RoleA => 1 == r.valueB // so we ignore someRole1 here while dispatching the call to update
          case _ => false
        })

      (+someCoreA).update("updated")

      Then("one role and the player instance should be updated correctly.")
      val actual1: String = someRole1.valueC
      val actual2: String = someRole2.valueC
      "valueC" shouldBe actual1
      "updated" shouldBe actual2
      (+someCoreA).valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("updated"))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Calling multi-argument method in roles") {
    Given("a player and a role instance in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
      val someRole = new RoleD()

      And("a play relationship")
      someCoreA play someRole

      When("updating role attributes")

      val expected1 = "updated"
      val expected2 = 1

      (+someCoreA).update(expected1, expected2)

      Then("the role and the player instance should be updated correctly.")
      val actual1 = someRole.valueA
      val actual2 = someRole.valueB
      expected1 shouldBe actual1
      expected2 shouldBe actual2
      (+someCoreA).valueA match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected1))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueB match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected2))
        case Left(error) => fail(error.toString)
      }
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
    Given("a player and a role instance in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
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

      And("the player instance should be updated correctly.")
      (+someCoreA).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedInt))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueDouble match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedDouble))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueFloat match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedFloat))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueLong match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedLong))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueShort match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedShort))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueByte match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedByte))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueChar match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedChar))
        case Left(error) => fail(error.toString)
      }
      (+someCoreA).valueBoolean match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedBoolean))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Playing a role multiple times (same instance) from different players") {
    Given("some players and role in a multi compartment")
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()

    new MultiCompartmentUnderTest() {
      implicit var dd = DispatchQuery.empty

      val someRole = new RoleA()
      And("a play relationship")
      someCoreA play someRole
      someCoreB play someRole

      When("updating role attributes")
      val expected = "updated"
      (+someCoreA).update(expected)
      (+someCoreB).update(expected)

      val actual1: String = someRole.valueC
      Then("the role and player instance should be updated correctly.")
      expected shouldBe actual1
      (+someCoreA).valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
        case Left(error) => fail(error.toString)
      }
      (+someCoreB).valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
        case Left(error) => fail(error.toString)
      }

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
    Given("a player and some roles in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
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

    class ACompartment extends CompartmentUnderTest {

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
    Given("a player and some roles in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
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

      val actualVal6: Int = someRoleE.valueInt
      Then("the value should be set correctly")
      actualVal6 shouldBe expectedVal

      (+someCoreA).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedVal))
        case Left(error) => fail(error.toString)
      }
      (+someRoleB).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedVal))
        case Left(error) => fail(error.toString)
      }
      (+someRoleC).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedVal))
        case Left(error) => fail(error.toString)
      }
      (+someRoleD).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedVal))
        case Left(error) => fail(error.toString)
      }
      (+someRoleE).valueInt match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expectedVal))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Handling null arguments for applyDynamic") {
    Given("a player and a role in a multi compartment")
    val someCoreA = new CoreA()

    new MultiCompartmentUnderTest() {
      val someRoleA = new RoleA()
      val expected: String = "valueC"
      And("a play relationship")
      val p = someCoreA play someRoleA
      p.valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
        case Left(error) => fail(error.toString)
      }

      When("passing null")
      Then("no exception should be thrown")
      p.update(null)
      And("the field should be set correctly")
      p.valueC match {
        case Right(returnValue) => returnValue shouldBe Seq(Right(null))
        case Left(error) => fail(error.toString)
      }
    }
  }

  scenario("Dropping roles when using deep roles") {

    class Core() {
      def a(): String = "a"
    }

    class RoleWithB() {
      def b(): String = "b"
    }

    class RoleWithC() {
      def c(): String = "c"
    }

    Given("a player and some roles in a multi compartment")
    val someCore = new Core()
    val roleWithB = new RoleWithB
    val roleWithC = new RoleWithC

    new MultiCompartmentUnderTest() {
      someCore play roleWithB

      (+someCore).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }

      (+someCore).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }

      roleWithB play roleWithC

      (+someCore).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+someCore).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }

      (+someCore).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }

      someCore.drop(roleWithB)

      (+someCore).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }

      (+roleWithB).a() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithC).a() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+someCore).b() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithB).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }

      (+roleWithC).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }

      (+someCore).c() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithB).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }

      someCore.play(roleWithB)

      (+someCore).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }

      (+someCore).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).b() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
        case Left(error) => fail(error.toString)
      }

      (+someCore).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithB).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
      (+roleWithC).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }

      roleWithB.remove()

      (+someCore).a() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("a"))
        case Left(error) => fail(error.toString)
      }

      (+roleWithB).a() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithC).a() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+someCore).b() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      roleWithB.b() shouldBe "b"

      (+roleWithC).b() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+someCore).c() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithB).c() match {
        case Right(_) => fail("Player should have no access anymore!")
        case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
      }

      (+roleWithC).c() match {
        case Right(returnValue) => returnValue shouldBe Seq(Right("c"))
        case Left(error) => fail(error.toString)
      }
    }
  }
}
