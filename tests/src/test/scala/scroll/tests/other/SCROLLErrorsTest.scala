package scroll.tests.other

import scroll.internal.errors.SCROLLErrors.IllegalRoleInvocationDispatch
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.TypeNotFound
import scroll.tests.AbstractSCROLLTest

class SCROLLErrorsTest extends AbstractSCROLLTest {

  test("TypeNotFound: String representation") {
    val name = "TestRole"
    val testObject = TypeNotFound(name)
    val expected = s"Type '$name' could not be found!"
    testObject.toString shouldBe expected
  }

  test("RoleNotFound: String representation (empty args)") {
    val coreName = "TestCore"
    val roleName = "TestRole"
    val testObject = RoleNotFound(coreName, roleName, Seq.empty)
    val expected = s"No role with '$roleName' could not be found for the player '$coreName'!"
    testObject.toString shouldBe expected
  }
  test("RoleNotFound: String representation (with some args)") {
    val coreName = "TestCore"
    val roleName = "TestRole"
    val args = Seq("A", "B", "C")
    val testObject = RoleNotFound(coreName, roleName, args)
    val expected = s"No role with '$roleName' could not be found for the player '$coreName' with the following parameters: ('A', 'B', 'C')"
    testObject.toString shouldBe expected
  }

  test("IllegalRoleInvocationDispatch: String representation (empty args)") {
    val targetName = "someMethod"
    val roleName = "TestRole"
    val testObject = IllegalRoleInvocationDispatch(roleName, targetName, Seq.empty)
    val expected = s"'$targetName' could not be executed on role type '$roleName'!"
    testObject.toString shouldBe expected
  }
  test("IllegalRoleInvocationDispatch: String representation (with some args)") {
    val targetName = "someMethod"
    val roleName = "TestRole"
    val args = Seq("A", "B", "C")
    val testObject = IllegalRoleInvocationDispatch(roleName, targetName, args)
    val expected = s"'$targetName' could not be executed on role type '$roleName' with the following parameters: ('A', 'B', 'C')"
    testObject.toString shouldBe expected
  }

}
