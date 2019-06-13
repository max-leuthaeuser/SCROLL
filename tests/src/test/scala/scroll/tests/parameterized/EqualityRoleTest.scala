package scroll.tests.parameterized

import scroll.tests.mocks._

class EqualityRoleTest extends AbstractParameterizedSCROLLTest {

  test("Player and Role equality (flat roles)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        val player = someCore play someRole
        player shouldBe player
        someCore shouldBe someCore
        player shouldBe someCore
        (+player) shouldBe player
        player shouldBe (+player)
        someRole shouldBe someRole
        (+someRole) shouldBe player
        player shouldBe (+someRole)
        (+someRole) shouldBe someCore
      } shouldNot be(null)
    }
  }

  test("Player and Role equality (chained deep roles)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        val someOtherRole = new RoleB()
        val player = (someCore play someRole) play someOtherRole
        player shouldBe player
        someCore shouldBe someCore
        player shouldBe someCore
        (+player) shouldBe player
        player shouldBe (+player)
        someRole shouldBe someRole
        someOtherRole shouldBe someOtherRole
        someRole should not be someOtherRole
        val a = +someRole
        val b = +someOtherRole
        a shouldBe player
        player shouldBe a
        b shouldBe player
        player shouldBe b
        (+someRole) shouldBe someCore
        (+someOtherRole) shouldBe someCore
      } shouldNot be(null)
    }
  }

  test("Player and Role equality (separate deep roles)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val someRole = new RoleA()
        val someOtherRole = new RoleB()
        val player = someCore play someRole
        someRole play someOtherRole
        player shouldBe player
        someCore shouldBe someCore
        player shouldBe someCore
        (+player) shouldBe player
        player shouldBe (+player)
        someRole shouldBe someRole
        someOtherRole shouldBe someOtherRole
        someRole should not be someOtherRole
        val a = +someRole
        val b = +someOtherRole
        a shouldBe player
        player shouldBe a
        b shouldBe player
        player shouldBe b
        (+someRole) shouldBe someCore
        (+someOtherRole) shouldBe someCore
      } shouldNot be(null)
    }
  }

}
