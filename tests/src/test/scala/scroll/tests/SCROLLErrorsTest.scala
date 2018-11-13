package scroll.tests

import scroll.internal.errors.SCROLLErrors.{IllegalRoleInvocationDispatch, RoleNotFound, TypeNotFound}

class SCROLLErrorsTest extends AbstractSCROLLTest(cached = true) {

  info("Test spec for SCROLLErrors.")

  feature("TypeNotFound") {
    scenario("String representation") {
      Given("A role name")
      val name = "TestRole"
      When("Creating a TypeNotFound error")
      val testObject = TypeNotFound(name)
      Then("The String representation should be correct")
      val expected = s"Type '$name' could not be found!"
      testObject.toString shouldBe expected
    }
  }

  feature("RoleNotFound") {
    scenario("String representation (empty args)") {
      Given("A core and role name")
      val coreName = "TestCore"
      val roleName = "TestRole"
      When("Creating a RoleNotFound error")
      val testObject = RoleNotFound(coreName, roleName, Seq.empty)
      Then("The String representation should be correct")
      val expected = s"No role with '$roleName' could not be found for the player '$coreName'!"
      testObject.toString shouldBe expected
    }
    scenario("String representation (with some args)") {
      Given("A core and role name")
      val coreName = "TestCore"
      val roleName = "TestRole"
      val args = Seq("A", "B", "C")
      When("Creating a RoleNotFound error")
      val testObject = RoleNotFound(coreName, roleName, args)
      Then("The String representation should be correct")
      val expected = s"No role with '$roleName' could not be found for the player '$coreName' with the following parameters: ('A', 'B', 'C')"
      testObject.toString shouldBe expected
    }
  }

  feature("IllegalRoleInvocationDispatch") {
    scenario("String representation (empty args)") {
      Given("A role name and a target name")
      val targetName = "someMethod"
      val roleName = "TestRole"
      When("Creating a IllegalRoleInvocationDispatch error")
      val testObject = IllegalRoleInvocationDispatch(roleName, targetName, Seq.empty)
      Then("The String representation should be correct")
      val expected = s"'$targetName' could not be executed on role type '$roleName'!"
      testObject.toString shouldBe expected
    }
    scenario("String representation (with some args)") {
      Given("A role name and a target name")
      val targetName = "someMethod"
      val roleName = "TestRole"
      val args = Seq("A", "B", "C")
      When("Creating a IllegalRoleInvocationDispatch error")
      val testObject = IllegalRoleInvocationDispatch(roleName, targetName, args)
      Then("The String representation should be correct")
      val expected = s"'$targetName' could not be executed on role type '$roleName' with the following parameters: ('A', 'B', 'C')"
      testObject.toString shouldBe expected
    }
  }

}
