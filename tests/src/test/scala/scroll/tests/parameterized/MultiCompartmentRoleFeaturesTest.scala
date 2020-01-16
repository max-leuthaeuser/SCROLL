package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.dispatch.DispatchQuery._
import scroll.tests.mocks._

class MultiCompartmentRoleFeaturesTest extends AbstractParameterizedSCROLLTest {

  test("Dropping role and invoking methods") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCore play someRole
        someCore play new RoleB()
        someCore drop someRole
        (+someCore).a() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(-1))
          case Left(error) => fail(error.toString)
        }
        (+someCore).isPlaying[RoleA] shouldBe false
        (+someCore).isPlaying[RoleB] shouldBe true
        (+someCore).b() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right("b"))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Transferring a role") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreA transfer someRole to someCoreB
        (+someCoreB).a() match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(0))
          case Left(error) => fail(error.toString)
        }
        (+someCoreA).isPlaying[RoleA] shouldBe false
        (+someCoreB).isPlaying[RoleA] shouldBe true
      }
    }
  }

  test("Role playing and testing isPlaying") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new MultiCompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleB = new RoleB()
        someCoreA play someRoleA
        someCoreA.isPlaying[RoleB] shouldBe false
        someCoreB.isPlaying[RoleA] shouldBe false
        someCoreB.isPlaying[RoleB] shouldBe false
        someCoreA.isPlaying[RoleA] shouldBe true
      }
    }
  }

  test("Handling applyDynamic") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        (+someCoreA).a[Int]() match {
          case Right(returnValue) => returnValue.head shouldBe Right(0)
          case Left(error) => fail(error.toString)
        }
        (+someCoreA).c() match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }
  }

  test("Handling applyDynamicNamed") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        val expected = someRole.b("some", param = "out")
        (+someCoreA).b("some", param = "out") match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Handling selectDynamic") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
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
        (+someCoreA).valueD match {
          case Left(_) => // correct
          case Right(_) => fail("A call to the role with a method that does not exist should fail")
        }
      }
    }
  }

  test("Handling updateDynamic") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val someRoleD = new RoleD()
        someCoreA play someRoleA play someRoleD
        val expectedA = "newValue"
        (+someCoreA).valueA = expectedA
        (+someCoreA).valueA[String] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedA), Right(expectedA))
          case Left(error) => fail(error.toString)
        }
        (+someRoleA).valueA[String] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedA), Right(expectedA))
          case Left(error) => fail(error.toString)
        }
        (+someRoleD).valueA[String] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedA), Right(expectedA))
          case Left(error) => fail(error.toString)
        }
        val expectedB = -1
        (+someCoreA).valueB = expectedB
        (+someCoreA).valueB[Int] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedB), Right(expectedB))
          case Left(error) => fail(error.toString)
        }
        (+someRoleA).valueB[Int] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedB), Right(expectedB))
          case Left(error) => fail(error.toString)
        }
        (+someRoleD).valueB[Int] match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expectedB), Right(expectedB))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Playing a role multiple times (same instance)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreA play someRole
        val expected = "updated"
        (+someCoreA).update(expected)
        val actual1: String = someRole.valueC
        expected shouldBe actual1
        (+someCoreA).valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Playing a role multiple times (different instances) from one player") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole1 = new RoleA()
        val someRole2 = new RoleA()
        someCoreA play someRole1
        someCoreA play someRole2
        val expected = "updated"
        (+someCoreA).update(expected)
        val actual1a: String = someRole1.valueC
        val actual1b: String = someRole2.valueC
        (expected == actual1a || expected == actual1b) shouldBe true
        (+someCoreA).valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected), Right(expected))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Playing a role multiple times (different instances, but using dispatch to select one)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
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
        "valueC" shouldBe actual1
        "updated" shouldBe actual2
        (+someCoreA).valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right("updated"))
          case Left(error) => fail(error.toString)
        }
      }
    }
  }

  test("Calling multi-argument method in roles") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRole = new RoleD()
        someCoreA play someRole
        val expected1 = "updated"
        val expected2 = 1
        (+someCoreA).update(expected1, expected2)
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
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
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
  }

  test("Playing a role multiple times (same instance) from different players") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new MultiCompartmentUnderTest(c, cc) {
        implicit var dd = DispatchQuery.empty
        val someRole = new RoleA()
        someCoreA play someRole
        someCoreB play someRole
        val expected = "updated"
        (+someCoreA).update(expected)
        (+someCoreB).update(expected)
        val actual1: String = someRole.valueC
        expected shouldBe actual1
        (+someCoreA).valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
        (+someCoreB).valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
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
      }
    }
  }

  test("Cyclic role-playing relationship") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      whenever(cc) {
        val someCoreA = new CoreA()
        new MultiCompartmentUnderTest(c, true) {
          val someRoleA = new RoleA()
          val someRoleB = new RoleB()
          val someRoleC = new RoleC()
          someCoreA play someRoleA
          someRoleA play someRoleB
          someRoleB play someRoleC
          a[RuntimeException] should be thrownBy {
            someRoleC play someRoleA
          }
        }
      }
    }
  }

  test("Compartment plays a role that is part of themselves") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      class ACompartment extends CompartmentUnderTest(c, cc) {

        class ARole

      }
      new ACompartment {
        this play new ARole
        this.isPlaying[ARole] shouldBe true
      }
    }
  }

  test("Deep roles") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
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
        val actualVal6: Int = someRoleE.valueInt
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
  }

  test("Handling null arguments for applyDynamic") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      new MultiCompartmentUnderTest(c, cc) {
        val someRoleA = new RoleA()
        val expected: String = "valueC"
        val p = someCoreA play someRoleA
        p.valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(expected))
          case Left(error) => fail(error.toString)
        }
        p.update(null)
        p.valueC match {
          case Right(returnValue) => returnValue shouldBe Seq(Right(null))
          case Left(error) => fail(error.toString)
        }
      }
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
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new Core()
      val roleWithB = new RoleWithB()
      val roleWithC = new RoleWithC()
      new MultiCompartmentUnderTest(c, cc) {
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

}
