package scroll.tests

import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.fail

import mocks.{CoreA, CoreB, SomeCompartment}
import scroll.internal.support.DispatchQuery
import DispatchQuery._
import scroll.internal.errors.SCROLLErrors

class RoleFeaturesTest {

  @Test
  def testDroppingRoleAndInvokingMethods(): Unit = {
    val someCore = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCore play someRole
      someCore play new RoleB()
      someCore drop someRole
      someCore a()
      +someCore a()
      assertFalse((+someCore).isPlaying[RoleA])
      assertTrue((+someCore).isPlaying[RoleB])
      val resB: String = +someCore b()
      assertEquals("b", resB)
    }
  }

  @Test
  def testTransferringARole(): Unit = {
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole
      someCoreA transfer someRole to someCoreB
      val res: Int = +someCoreB a()
      assertEquals(0, res)
      assertFalse((+someCoreA).isPlaying[RoleA])
      assertTrue((+someCoreB).isPlaying[RoleA])
    }
  }

  @Test
  def testRolePlayingAndTestingIsPlaying(): Unit = {
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()
    new SomeCompartment() {
      val someRoleA = new RoleA()
      val someRoleB = new RoleB()
      someCoreA play someRoleA
      assertFalse(someCoreA.isPlaying[RoleB])
      assertFalse(someCoreB.isPlaying[RoleA])
      assertFalse(someCoreB.isPlaying[RoleB])
      assertTrue(someCoreA.isPlaying[RoleA])
    }
  }

  @Test
  def testHandlingApplyDynamic(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole
      val expected = 0
      val actual: Int = +someCoreA a()

      assertEquals(expected, actual)

      val r: Either[SCROLLErrors.SCROLLError, Nothing] = +someCoreA c()
      r match {
        case Left(_) => // correct
        case Right(_) => fail("A call to the role with a method that does not exist should fail")
      }
    }
  }

  @Test
  def testHandlingApplyDynamicNamed(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole
      val expected: String = someRole.b("some", param = "out")
      val actual: String = +someCoreA b("some", param = "out")

      assertEquals(expected, actual)
    }
  }

  @Test
  def testHandlingSelectDynamic(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole

      val expectedA: String = someRole.valueA
      val actualA: String = (+someCoreA).valueA
      val expectedB: Int = someRole.valueB
      val actualB: Int = (+someCoreA).valueB

      assertEquals(expectedA, actualA)
      assertEquals(expectedB, actualB)

      val r: Either[SCROLLErrors.SCROLLError, Nothing] = (+someCoreA).valueD
      r match {
        case Left(_) => // correct
        case Right(_) => fail("A call to the role with a method that does not exist should fail")
      }
    }
  }

  @Test
  def testHandlingUpdateDynamic(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole
      val expectedA = "newValue"
      (+someCoreA).valueA = expectedA
      val actualA: String = (+someCoreA).valueA
      val expectedB: Int = -1
      (+someCoreA).valueB = expectedB
      val actualB: Int = (+someCoreA).valueB

      assertEquals(expectedA, actualA)
      assertEquals(expectedB, actualB)
    }
  }

  @Test
  def testPlayingARoleMultipleTimesSameInstance(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCoreA play someRole
      someCoreA play someRole
      val expected = "updated"
      (+someCoreA).update(expected)
      val actual1: String = someRole.valueC
      val actual2: String = (+someCoreA).valueC

      assertEquals(expected, actual1)
      assertEquals(expected, actual2)
    }
  }

  @Test
  def testPlayingARoleMultipleTimesDifferentInstance(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      someCoreA play someRole1
      someCoreA play someRole2
      val expected = "updated"
      (+someCoreA).update(expected)
      val actual1a: String = someRole1.valueC
      val actual1b: String = someRole2.valueC
      val actual2: String = (+someCoreA).valueC

      assertTrue(expected == actual1a || expected == actual1b)
      assertEquals(expected, actual2)
    }
  }

  @Test
  def testPlayingARoleMultipleTimesDifferentInstanceWithDispatch(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole1 = new RoleA()
      val someRole2 = new RoleA()
      someRole1.valueB = 1
      someRole2.valueB = 2
      someCoreA play someRole1
      someCoreA play someRole2
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

      assertEquals("valueC", actual1)
      assertEquals("updated", actual2)
      assertEquals("updated", actual3)
    }
  }

  @Test
  def testCallingMultiArgumentMethodInRoles(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleD()
      someCoreA play someRole
      val expected1 = "updated"
      val expected2 = 1
      (+someCoreA).update(expected1, expected2)
      val actual1: String = someRole.valueA
      val actual2: Int = someRole.valueB
      val actual3: String = (+someCoreA).valueA
      val actual4: Int = (+someCoreA).valueB

      assertEquals(expected1, actual1)
      assertEquals(expected2, actual2)
      assertEquals(expected1, actual3)
      assertEquals(expected2, actual4)
    }
  }

  @Test
  def testCallingMethodOnARoleWithDifferentPrimitiveTypes(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
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

      val actualIntR: Int = someRole.valueInt
      val actualDoubleR: Double = someRole.valueDouble
      val actualFloatR: Float = someRole.valueFloat
      val actualLongR: Long = someRole.valueLong
      val actualShortR: Short = someRole.valueShort
      val actualByteR: Byte = someRole.valueByte
      val actualCharR: Char = someRole.valueChar
      val actualBooleanR: Boolean = someRole.valueBoolean

      assertEquals(expectedInt, actualIntR)
      assertEquals(expectedDouble, actualDoubleR, 0)
      assertEquals(expectedFloat, actualFloatR)
      assertEquals(expectedLong, actualLongR)
      assertEquals(expectedShort, actualShortR)
      assertEquals(expectedByte, actualByteR)
      assertEquals(expectedChar, actualCharR)
      assertEquals(expectedBoolean, actualBooleanR)

      val actualIntP: Int = (+someCoreA).valueInt
      val actualDoubleP: Double = (+someCoreA).valueDouble
      val actualFloatP: Float = (+someCoreA).valueFloat
      val actualLongP: Long = (+someCoreA).valueLong
      val actualShortP: Short = (+someCoreA).valueShort
      val actualByteP: Byte = (+someCoreA).valueByte
      val actualCharP: Char = (+someCoreA).valueChar
      val actualBooleanP: Boolean = (+someCoreA).valueBoolean

      assertEquals(expectedInt, actualIntP)
      assertEquals(expectedDouble, actualDoubleP, 0)
      assertEquals(expectedFloat, actualFloatP)
      assertEquals(expectedLong, actualLongP)
      assertEquals(expectedShort, actualShortP)
      assertEquals(expectedByte, actualByteP)
      assertEquals(expectedChar, actualCharP)
      assertEquals(expectedBoolean, actualBooleanP)
    }
  }

  @Test
  def testPlayingARoleMultipleTimes(): Unit = {
    val someCoreA = new CoreA()
    val someCoreB = new CoreB()
    new SomeCompartment() {
      implicit var dd: DispatchQuery = DispatchQuery.empty
      val someRole = new RoleA()
      someCoreA play someRole
      someCoreB play someRole
      val expected = "updated"
      (+someCoreA).update(expected)
      (+someCoreB).update(expected)
      val actual1: String = someRole.valueC
      val actual2: String = (+someCoreA).valueC
      val actual3: String = (+someCoreB).valueC

      assertEquals(expected, actual1)
      assertEquals(expected, actual2)
      assertEquals(expected, actual3)

      val player: AnyRef = someRole.player match {
        case Left(_) => fail("Player should be defined here!"); null
        case Right(p) => p
      }

      assertTrue(player == someCoreA || player == someCoreB)

      dd = From(anything).
        To(c => c.isInstanceOf[CoreA] || c.isInstanceOf[CoreB]).
        Through(anything).
        Bypassing(_.isInstanceOf[CoreB])
      val player2: AnyRef = someRole.player match {
        case Left(_) => fail("Player should be defined here!"); null
        case Right(p) => p
      }

      assertEquals(player2, someCoreA)
    }
  }

  @Test
  def testCyclicRolePlaying(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRoleA = new RoleA()
      val someRoleB = new RoleB()
      val someRoleC = new RoleC()
      someCoreA play someRoleA
      someRoleA play someRoleB
      someRoleB play someRoleC

      try {
        someRoleC play someRoleA
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testCompartmentPlaysARoleThatIsPartOfItself(): Unit = {
    class ACompartment extends SomeCompartment {

      class ARole

    }
    new ACompartment {
      this play new ARole
      assertTrue(this.isPlaying[ARole])
    }
  }

  @Test
  def testDeepRoles(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
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

      assertEquals(expectedVal, actualVal1)
      assertEquals(expectedVal, actualVal2)
      assertEquals(expectedVal, actualVal3)
      assertEquals(expectedVal, actualVal4)
      assertEquals(expectedVal, actualVal5)
      assertEquals(expectedVal, actualVal6)
    }
  }

  @Test
  def testHandlingNullArguments(): Unit = {
    val someCoreA = new CoreA()
    new SomeCompartment() {
      val someRoleA = new RoleA()
      val expected: String = "valueC"
      val p = someCoreA play someRoleA
      var actual: String = p.valueC

      assertEquals(expected, actual)

      p.update(null)
      actual = p.valueC

      assertNull(actual)
    }
  }
}