package scroll.tests.parameterized

import scroll.internal.dispatch.DispatchQuery
import scroll.tests.mocks.CompartmentUnderTest
import scroll.tests.mocks.MultiCompartmentUnderTest
import scroll.tests.mocks.RoleA

class BuiltinsTest extends AbstractParameterizedSCROLLTest {

  class CoreWithBuiltins() {
    override def hashCode(): Int = 0

    override def toString: String = "Core"
  }

  class RoleWithBuiltins() {
    override def hashCode(): Int = 1

    override def toString: String = "Role"
  }

  test("Native Player and Role builtins") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val core = new CoreWithBuiltins()
        val role = new RoleWithBuiltins()

        core should not be role
        role should not be core

        core.hashCode() shouldBe 0
        core shouldBe core

        role.hashCode() shouldBe 1
        role shouldBe role

        core.toString shouldBe "Core"
        core shouldBe core

        role.toString shouldBe "Role"
        role shouldBe role
      }
    }
  }

  test("Player with Role builtins") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val core = new CoreWithBuiltins()
        val role = new RoleWithBuiltins()

        val player = core play role

        core should not be role
        role should not be core
        player shouldBe player
        player shouldBe +role

        core.hashCode() shouldBe 0
        core shouldBe core

        role.hashCode() shouldBe 1
        role shouldBe role

        core.toString shouldBe "Core"
        core shouldBe core

        role.toString shouldBe "Role"
        role shouldBe role

        player.hashCode() shouldBe 1
        player.toString() shouldBe "Role"
      }
    }
  }

  test("Player with Role builtins (MultiCompartment") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiCompartmentUnderTest(c, cc) {
        val core = new CoreWithBuiltins()
        val role = new RoleWithBuiltins()

        val player = core play role

        core should not be role
        role should not be core
        player shouldBe player
        player shouldBe +role

        core.hashCode() shouldBe 0
        core shouldBe core

        role.hashCode() shouldBe 1
        role shouldBe role

        core.toString shouldBe "Core"
        core shouldBe core

        role.toString shouldBe "Role"
        role shouldBe role

        player.hashCode() shouldBe Seq(1, 0)
        player.toString() shouldBe Seq("Role", "Core")
      }
    }
  }

  test("Player without Role builtins (defaulting back to core)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new CompartmentUnderTest(c, cc) {
        val core = new CoreWithBuiltins()
        val role = new RoleA()

        val player = core play role

        core should not be role
        role should not be core
        player shouldBe player
        player shouldBe +role

        core.hashCode() shouldBe 0
        core shouldBe core

        role.hashCode() should not be 1
        role shouldBe role

        core.toString shouldBe "Core"
        core shouldBe core

        role.toString should not be "Role"
        role shouldBe role

        // we need to bypass bound instances of RoleA here. Otherwise, its hashCode and toString
        // methods from Object would be called.
        implicit val dd: DispatchQuery = DispatchQuery.Bypassing(_.isInstanceOf[RoleA])
        player.hashCode() shouldBe 0
        player.toString() shouldBe "Core"
      }
    }
  }

  test("Player without Role builtins (defaulting back to core, MultiComparment)") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      new MultiCompartmentUnderTest(c, cc) {
        val core = new CoreWithBuiltins()
        val role = new RoleA()

        val player = core play role

        core should not be role
        role should not be core
        player shouldBe player
        player shouldBe +role

        core.hashCode() shouldBe 0
        core shouldBe core

        role.hashCode() should not be 1
        role shouldBe role

        core.toString shouldBe "Core"
        core shouldBe core

        role.toString should not be "Role"
        role shouldBe role

        // we need to bypass bound instances of RoleA here. Otherwise, its hashCode and toString
        // methods from Object would be called.
        implicit val dd: DispatchQuery = DispatchQuery.Bypassing(_.isInstanceOf[RoleA])
        player.hashCode() shouldBe Seq(0)
        player.toString() shouldBe Seq("Core")
      }
    }
  }

}
