package scroll.tests.other

import scroll.internal.errors.SCROLLErrors.IllegalRoleInvocationDispatch
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.tests.AbstractSCROLLTest
import scroll.tests.mocks.CoreA
import scroll.tests.mocks.RoleA

class SCROLLErrorsTest extends AbstractSCROLLTest {

  test("TypeNotFound: String representation") {
    val tpe        = new RoleA
    val testObject = TypeNotFound(tpe.getClass)
    val expected   = s"Type '${tpe.getClass}' could not be found!"
    testObject.toString shouldBe expected
  }

  test("RoleNotFound: String representation (empty args)") {
    val tpe        = new CoreA
    val targetName = "method"
    val testObject = RoleNotFound(tpe, targetName, Seq.empty)
    val expected   = s"No role with '$targetName' could not be found for the player '$tpe'!"
    testObject.toString shouldBe expected
  }

  test("RoleNotFound: String representation (with some args)") {
    val tpe        = new CoreA
    val targetName = "method"
    val args       = Seq("A", "B", "C")
    val testObject = RoleNotFound(tpe, targetName, args)
    val expected =
      s"No role with '$targetName' could not be found for the player '$tpe' with the following parameters: ('A', 'B', 'C')"
    testObject.toString shouldBe expected
  }

  test("IllegalRoleInvocationDispatch: String representation (empty args)") {
    val tpe        = new RoleA
    val targetName = "someMethod"
    val testObject = IllegalRoleInvocationDispatch(tpe, targetName, Seq.empty)
    val expected   = s"'$targetName' could not be executed on role type '$tpe'!"
    testObject.toString shouldBe expected
  }

  test("IllegalRoleInvocationDispatch: String representation (with some args)") {
    val tpe        = new RoleA
    val targetName = "someMethod"
    val args       = Seq("A", "B", "C")
    val testObject = IllegalRoleInvocationDispatch(tpe, targetName, args)
    val expected =
      s"'$targetName' could not be executed on role type '$tpe' with the following parameters: ('A', 'B', 'C')"
    testObject.toString shouldBe expected
  }

}
