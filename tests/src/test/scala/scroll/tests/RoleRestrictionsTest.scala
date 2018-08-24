package scroll.tests

import org.junit.Test
import mocks.{CoreA, SomeCompartment}
import org.junit.Assert.fail

class RoleRestrictionsTest {

  @Test
  def testRoleRestrictionValidation(): Unit = {
    val player = new CoreA()
    new SomeCompartment() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      player play roleA
      player drop roleA
      ReplaceRoleRestriction[CoreA, RoleD]()
      try {
        player play roleA
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testRoleRestrictionValidationOnMultipleTypes(): Unit = {
    val player = new CoreA()
    new SomeCompartment() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      AddRoleRestriction[CoreA, RoleD]()
      player play roleA
      player play roleD
      try {
        player play new RoleB()
        fail("Should throw an RuntimeException")
      } catch {
        case _: RuntimeException => // all good
      }
    }
  }

  @Test
  def testRoleRestrictionValidationAfterRemoval(): Unit = {
    val player = new CoreA()
    new SomeCompartment() {
      val roleA = new RoleA()
      val roleD = new RoleD()
      AddRoleRestriction[CoreA, RoleA]()
      player play roleA
      RemoveRoleRestriction[CoreA]()
      player play roleD
      player drop roleA drop roleD
      AddRoleRestriction[CoreA, RoleA]()
      AddRoleRestriction[CoreA, RoleD]()
      player play roleA play roleD
      player drop roleA drop roleD
      RemoveRoleRestriction[CoreA]()
      player play roleA play roleD
    }
  }
}
