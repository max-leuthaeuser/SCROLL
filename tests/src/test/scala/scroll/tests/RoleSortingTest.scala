package scroll.tests

import org.junit.Test
import org.junit.Assert.assertEquals

import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._
import scroll.tests.mocks.{CoreA, SomeCompartment}

class RoleSortingTest {

  @Test
  def testRoleSorting(): Unit = {
    implicit var dd: DispatchQuery = DispatchQuery.empty

    val someCore = new CoreA()
    new SomeCompartment() {

      class SomeRoleA() {
        def method(): String = "A"
      }

      class SomeRoleB() {
        def method(): String = "B"
      }

      class SomeRoleC() {
        def method(): String = "C"
      }

      val roleA = new SomeRoleA()
      val roleB = new SomeRoleB()
      val roleC = new SomeRoleC()
      someCore play roleA
      roleA play roleB
      roleB play roleC

      val r1: String = +someCore method()
      assertEquals("C", r1)

      dd = DispatchQuery.empty.sortedWith(reverse)
      val r2: String = +someCore method()
      assertEquals("A", r2)

      dd = DispatchQuery.empty.sortedWith {
        case (_: SomeRoleB, _: SomeRoleC) => swap
      }
      val r3: String = +someCore method()
      assertEquals("B", r3)

      dd = Bypassing(_.isInstanceOf[SomeRoleA]).sortedWith {
        case (_: SomeRoleB, _: SomeRoleC) => swap
      }
      val r4: String = +someCore method()
      assertEquals("B", r4)
    }
  }

  @Test
  def testRoleSortingWithCycles(): Unit = {
    implicit var dd: DispatchQuery = DispatchQuery.empty

    class SomeCore {
      def method(): String = "Core"
    }

    val someCore = new SomeCore()
    new SomeCompartment() {

      class SomeRoleA() {
        def method(): String = {
          dd = Bypassing(_.isInstanceOf[SomeRoleA])
          +this method()
        }
      }

      class SomeRoleB() {
        def method(): String = {
          dd = DispatchQuery.empty.sortedWith(reverse)
          +this method()
        }
      }

      val roleA = new SomeRoleA()
      val roleB = new SomeRoleB()
      someCore play roleA
      roleA play roleB

      val r1: String = +someCore method()
      assertEquals("Core", r1)
    }
  }
}