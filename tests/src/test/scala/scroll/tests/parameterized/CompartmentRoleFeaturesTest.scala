package scroll.tests.parameterized

import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._
import scroll.tests.mocks._

class CompartmentRoleFeaturesTest extends AbstractParameterizedSCROLLTest {

  test("Dropping role and invoking methods") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCore play someRole
        someCore play new RoleB()
        someCore drop someRole
        someCore.a()
        +someCore a()
        (+someCore).isPlaying[RoleA] shouldBe false
        (+someCore).isPlaying[RoleB] shouldBe true
        val resB: String = (+someCore).b()
        resB shouldBe "b"
      } shouldNot be(null)
    }
  }

  test("Dropping role and invoking methods with alias methods") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCore <+> someRole
        someCore <+> new RoleB()
        someCore <-> someRole
        someCore.a()
        +someCore a()
        (+someCore).isPlaying[RoleA] shouldBe false
        (+someCore).isPlaying[RoleB] shouldBe true
        val resB: String = (+someCore).b()
        resB shouldBe "b"
      } shouldNot be(null)
    }
  }

  test("Removing a player using the compartment") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCore play someRole
        this.removePlayer(someCore)
        (+someCore).isPlaying[RoleA] shouldBe false
        +someCore s() match {
          case Right(_) => fail("Player should have no access anymore!")
          case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
        }
      } shouldNot be(null)
    }
  }

  test("Calling allPlayers using the compartment") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val roleA = new RoleA()
        val roleB = new RoleB()
        someCore play roleA
        someCore play roleB
        val expected = Seq(someCore, roleA, roleB)
        this.allPlayers shouldBe expected
      } shouldNot be(null)
    }
  }

  test("Transferring a role") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreA transfer someRole to someCoreB
        val res: Int = (+someCoreB).a()
        res shouldBe 0
        (+someCoreA).isPlaying[RoleA] shouldBe false
        (+someCoreB).isPlaying[RoleA] shouldBe true
      } shouldNot be(null)
    }
  }

  test("Role playing and testing isPlaying") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new CompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        someCoreA play someRoleA
        someCoreA.isPlaying[RoleB] shouldBe false
        someCoreB.isPlaying[RoleA] shouldBe false
        someCoreB.isPlaying[RoleB] shouldBe false
        someCoreA.isPlaying[RoleA] shouldBe true
      } shouldNot be(null)
    }
  }

  test("Handling applyDynamic") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        val expected = 0
        val actual: Int = (+someCoreA).a()
        expected shouldBe actual
        val r = (+someCoreA).c()
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      } shouldNot be(null)
    }
  }

  test("Handling applyDynamicNamed") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        val expected = someRole.b("some", param = "out")
        val actual: String = (+someCoreA).b("some", param = "out")
        expected shouldBe actual
      } shouldNot be(null)
    }
  }

  test("Handling selectDynamic") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        val expectedA = someRole.valueA
        val actualA: String = (+someCoreA).valueA
        val expectedB = someRole.valueB
        val actualB: Int = (+someCoreA).valueB
        expectedA shouldBe actualA
        expectedB shouldBe actualB
        val r = (+someCoreA).valueD
        r match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      } shouldNot be(null)
    }
  }

  test("Handling updateDynamic") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        val expectedA = "newValue"
        (+someCoreA).valueA = expectedA
        val actualA: String = (+someCoreA).valueA
        val expectedB = -1
        (+someCoreA).valueB = expectedB
        val actualB: Int = (+someCoreA).valueB
        expectedA shouldBe actualA
        expectedB shouldBe actualB
      } shouldNot be(null)
    }
  }

  test("Playing a role multiple times (same instance)") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreA play someRole
        val expected = "updated"
        (+someCoreA).update(expected)
        val actual1: String = someRole.valueC
        val actual2: String = (+someCoreA).valueC
        expected shouldBe actual1
        expected shouldBe actual2
      } shouldNot be(null)
    }
  }

  test("Playing a role multiple times (different instances) from one player") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        someCoreA play someRole1
        someCoreA play someRole2
        val expected = "updated"
        (+someCoreA).update(expected)
        val actual1a: String = someRole1.valueC
        val actual1b: String = someRole2.valueC
        val actual2: String = (+someCoreA).valueC
        (expected == actual1a || expected == actual1b) shouldBe true
        expected shouldBe actual2
      } shouldNot be(null)
    }
  }

  test("Playing a role multiple times (different instances, but using dispatch to select one)") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        someRole1.valueB = 1
        someRole2.valueB = 2
        someCoreA play someRole1
        someCoreA play someRole2
        implicit val dd = From(_.isInstanceOf[CoreA]).
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
        "valueC" shouldBe actual1
        "updated" shouldBe actual2
        "updated" shouldBe actual3
      } shouldNot be(null)
    }
  }

  test("Calling multi-argument method in roles") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleD()
        someCoreA play someRole
        val expected1 = "updated"
        val expected2 = 1
        (+someCoreA).update(expected1, expected2)
        val actual1 = someRole.valueA
        val actual2 = someRole.valueB
        val actual3: String = (+someCoreA).valueA
        val actual4: Int = (+someCoreA).valueB
        expected1 shouldBe actual1
        expected2 shouldBe actual2
        expected1 shouldBe actual3
        expected2 shouldBe actual4
      } shouldNot be(null)
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
  test("Calling method on a role with different primitive types") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleE()
        someCoreA play someRole
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
        actualIntP shouldBe expectedInt
        actualDoubleP shouldBe expectedDouble
        actualFloatP shouldBe expectedFloat
        actualLongP shouldBe expectedLong
        actualShortP shouldBe expectedShort
        actualByteP shouldBe expectedByte
        actualCharP shouldBe expectedChar
        actualBooleanP shouldBe expectedBoolean
      } shouldNot be(null)
    }
  }

  test("Playing a role multiple times (same instance) from different players") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new CompartmentUnderTest(c, cc) {
        implicit var dd = DispatchQuery.empty
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreB play someRole
        val expected = "updated"
        (+someCoreA).update(expected)
        (+someCoreB).update(expected)
        val actual1: String = someRole.valueC
        val actual2: String = (+someCoreA).valueC
        val actual3: String = (+someCoreB).valueC
        expected shouldBe actual1
        expected shouldBe actual2
        expected shouldBe actual3
        val player = someRole.player match {
          case Left(_) => fail("Player should be defined here!")
          case Right(p) => p
        }
        (player == someCoreA || player == someCoreB) shouldBe true
        dd = From(anything).
          To(c => c.isInstanceOf[CoreA] || c.isInstanceOf[CoreB]).
          Through(anything).
          Bypassing(_.isInstanceOf[CoreB])
        val player2 = someRole.player match {
          case Left(_) => fail("Player should be defined here!")
          case Right(p) => p
        }
        player2 shouldBe someCoreA
      } shouldNot be(null)
    }
  }

  test("Cyclic role-playing relationship") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      whenever(cc) {
        val someCoreA = new CoreA()
        new CompartmentUnderTest(c, true) {
          val someRoleA = new RoleA()
          val someRoleB = new RoleB()
          val someRoleC = new RoleC()
          someCoreA play someRoleA
          someRoleA play someRoleB
          someRoleB play someRoleC
          a[RuntimeException] should be thrownBy {
            someRoleC play someRoleA
          }
        } shouldNot be(null)
      }
    }
  }

  test("Compartment plays a role that is part of themselves") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      class ACompartment(c: Boolean, cc: Boolean) extends CompartmentUnderTest(c, cc) {

        class ARole

      }
      new ACompartment(c, cc) {
        this play new ARole
        this.isPlaying[ARole] shouldBe true
      } shouldNot be(null)
    }
  }

  test("Deep roles") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        val someRoleC = new RoleC()
        val someRoleD = new RoleD()
        val someRoleE = new RoleE()
        val expectedVal = 10
        someCoreA play someRoleA
        someRoleA play someRoleB
        someRoleB play someRoleC
        someRoleC play someRoleD
        someRoleD play someRoleE
        (+someCoreA).valueInt = expectedVal
        val actualVal1: Int = (+someCoreA).valueInt
        val actualVal2: Int = (+someRoleB).valueInt
        val actualVal3: Int = (+someRoleC).valueInt
        val actualVal4: Int = (+someRoleD).valueInt
        val actualVal5: Int = (+someRoleE).valueInt
        val actualVal6: Int = someRoleE.valueInt
        actualVal1 shouldBe expectedVal
        actualVal2 shouldBe expectedVal
        actualVal3 shouldBe expectedVal
        actualVal4 shouldBe expectedVal
        actualVal5 shouldBe expectedVal
        actualVal6 shouldBe expectedVal
        val expected = Seq(someRoleD, someRoleC, someRoleB, someRoleA, someCoreA)
        someRoleE.predecessors() shouldBe expected
      } shouldNot be(null)
    }
  }

  test("Deep roles (chained directly)") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        val someRoleC = new RoleC()
        val someRoleD = new RoleD()
        val someRoleE = new RoleE()
        val expectedVal = 10
        someCoreA playing someRoleA playing someRoleB playing someRoleC playing someRoleD playing someRoleE
        (+someCoreA).valueInt = expectedVal
        val actualVal1: Int = (+someCoreA).valueInt
        val actualVal2: Int = (+someRoleB).valueInt
        val actualVal3: Int = (+someRoleC).valueInt
        val actualVal4: Int = (+someRoleD).valueInt
        val actualVal5: Int = (+someRoleE).valueInt
        val actualVal6: Int = someRoleE.valueInt
        actualVal1 shouldBe expectedVal
        actualVal2 shouldBe expectedVal
        actualVal3 shouldBe expectedVal
        actualVal4 shouldBe expectedVal
        actualVal5 shouldBe expectedVal
        actualVal6 shouldBe expectedVal
        val expected = Seq(someCoreA)
        someRoleE.predecessors() shouldBe expected
      } shouldNot be(null)
    }
  }

  test("Deep roles (chained directly with alias method)") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        val someRoleC = new RoleC()
        val someRoleD = new RoleD()
        val someRoleE = new RoleE()
        val expectedVal = 10
        someCoreA <=> someRoleA <=> someRoleB <=> someRoleC <=> someRoleD <=> someRoleE
        (+someCoreA).valueInt = expectedVal
        val actualVal1: Int = (+someCoreA).valueInt
        val actualVal2: Int = (+someRoleB).valueInt
        val actualVal3: Int = (+someRoleC).valueInt
        val actualVal4: Int = (+someRoleD).valueInt
        val actualVal5: Int = (+someRoleE).valueInt
        val actualVal6: Int = someRoleE.valueInt
        actualVal1 shouldBe expectedVal
        actualVal2 shouldBe expectedVal
        actualVal3 shouldBe expectedVal
        actualVal4 shouldBe expectedVal
        actualVal5 shouldBe expectedVal
        actualVal6 shouldBe expectedVal
        val expected = Seq(someCoreA)
        someRoleE.predecessors() shouldBe expected
      } shouldNot be(null)
    }
  }

  test("Handling null arguments for applyDynamic") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val expected: String = "valueC"
        val p = someCoreA play someRoleA
        var actual: String = p.valueC
        actual shouldBe expected
        p.update(null)
        actual = p.valueC
        actual shouldBe null
      } shouldNot be(null)
    }
  }

  test("Dropping roles when using deep roles") {
    class Core() {
      def a(): String = "a"
    }
    class RoleWithB() {
      def b(): String = "b"
    }
    class RoleWithC() {
      def c(): String = "c"
    }

    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val someCore = new Core()
      val roleWithB = new RoleWithB()
      val roleWithC = new RoleWithC()
      new CompartmentUnderTest(c, cc) {
        someCore play roleWithB
        var actual: String = (+someCore).a()
        actual shouldBe "a"
        actual = (+roleWithB).a()
        actual shouldBe "a"
        actual = (+someCore).b()
        actual shouldBe "b"
        actual = (+roleWithB).b()
        actual shouldBe "b"
        roleWithB play roleWithC
        actual = (+someCore).a()
        actual shouldBe "a"
        actual = (+roleWithB).a()
        actual shouldBe "a"
        actual = (+roleWithC).a()
        actual shouldBe "a"
        actual = (+someCore).b()
        actual shouldBe "b"
        actual = (+roleWithB).b()
        actual shouldBe "b"
        actual = (+roleWithC).b()
        actual shouldBe "b"
        actual = (+someCore).c()
        actual shouldBe "c"
        actual = (+roleWithB).c()
        actual shouldBe "c"
        actual = (+roleWithC).c()
        actual shouldBe "c"
        someCore.drop(roleWithB)
        actual = (+someCore).a()
        actual shouldBe "a"
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
        actual = (+roleWithB).b()
        actual shouldBe "b"
        actual = (+roleWithC).b()
        actual shouldBe "b"
        (+someCore).c() match {
          case Right(_) => fail("Player should have no access anymore!")
          case Left(err) if err.isInstanceOf[RoleNotFound] => // this is fine
        }
        actual = (+roleWithB).c()
        actual shouldBe "c"
        actual = (+roleWithC).c()
        actual shouldBe "c"
        someCore.play(roleWithB)
        actual = (+someCore).a()
        actual shouldBe "a"
        actual = (+roleWithB).a()
        actual shouldBe "a"
        actual = (+roleWithC).a()
        actual shouldBe "a"
        actual = (+someCore).b()
        actual shouldBe "b"
        actual = (+roleWithB).b()
        actual shouldBe "b"
        actual = (+roleWithC).b()
        actual shouldBe "b"
        actual = (+someCore).c()
        actual shouldBe "c"
        actual = (+roleWithB).c()
        actual shouldBe "c"
        actual = (+roleWithC).c()
        actual shouldBe "c"
        roleWithB.remove()
        actual = (+someCore).a()
        actual shouldBe "a"
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
        actual = roleWithB.b()
        actual shouldBe "b"
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
        actual = (+roleWithC).c()
        actual shouldBe "c"
      } shouldNot be(null)
    }
  }

}