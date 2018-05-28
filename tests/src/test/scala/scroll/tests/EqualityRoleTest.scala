package scroll.tests

import mocks.{CoreA, SomeCompartment}
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals

class EqualityRoleTest {

  @Test
  def testRoleEquality(): Unit = {
    val someCore = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      val player = someCore play someRole

      assertEquals(player, player)
      assertEquals(someCore, someCore)
      assertEquals(player, someCore)


      assertEquals(+player, player)
      assertEquals(player, +player)

      assertEquals(someRole, someRole)

      assertEquals(+someRole, player)
      assertEquals(player, +someRole)

      assertEquals(+someRole, someCore)
    }
  }

  @Test
  def testRoleEqualityDeepRoles(): Unit = {
    val someCore = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      val someOtherRole = new RoleB()
      val player = (someCore play someRole) play someOtherRole

      assertEquals(player, player)
      assertEquals(someCore, someCore)
      assertEquals(player, someCore)

      assertEquals(+player, player)
      assertEquals(player, +player)

      assertEquals(someRole, someRole)
      assertEquals(someOtherRole, someOtherRole)

      assertNotEquals(someRole.hashCode(), someOtherRole.hashCode())

      val a = +someRole
      val b = +someOtherRole
      assertEquals(a, player)
      assertEquals(player, a)
      assertEquals(b, player)
      assertEquals(player, b)

      assertEquals(+someRole, someCore)
      assertEquals(+someOtherRole, someCore)
    }
  }
}
