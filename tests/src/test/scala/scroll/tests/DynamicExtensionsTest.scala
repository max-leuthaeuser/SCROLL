package scroll.tests

import scroll.tests.mocks.{CoreA, SomeCompartment}
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

class DynamicExtensionsTest {

  @Test
  def testAddingDynamicExtensions(): Unit = {
    val someCore = new CoreA()
    new SomeCompartment() {
      val someRole = new RoleA()
      someCore <+> someRole
      someCore <+> new RoleB()

      someCore <-> someRole

      someCore a()
      +someCore a()

      assertFalse((+someCore).hasExtension[RoleA])
      assertTrue((+someCore).hasExtension[RoleB])

      val resB: String = +someCore b()
      assertEquals("b", resB)
    }
  }

}