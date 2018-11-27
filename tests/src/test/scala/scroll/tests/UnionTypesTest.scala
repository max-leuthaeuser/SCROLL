package scroll.tests

import mocks._

class UnionTypesTest(cached: Boolean) extends AbstractSCROLLTest(cached) {
  info("Test spec for union types in the context of roles.")

  feature("Simple method invocation") {
    scenario("Calling some role method directly") {
      Given("some players and role in a compartment")
      val someCoreA = new CoreA()
      new CompartmentUnderTest() {
        val someRoleC = new RoleC()

        And("a play relationship")
        someCoreA play someRoleC
        When("When calling a role method with union role types")
        Then("There should be no error or exception.")
        And("The result should be correct.")
        val expectedI = 0
        val expectedS = 4
        val actualI: Int = someRoleC.unionTypedMethod(0)
        val actualS: Int = someRoleC.unionTypedMethod("four")
        expectedI shouldBe actualI
        expectedS shouldBe actualS
      }
    }

    scenario("Matching players and roles with Scala match") {
      Given("some players and role in a compartment")
      val someCore = new CoreA()
      new CompartmentUnderTest() {
        val roleA = new RoleA

        val matcher = new {
          def m[T: (CoreA or RoleA)#λ](param: T) = param match {
            case _: CoreA => -1
            case a: RoleA => a.a()
          }

        }

        And("a play relationship")
        someCore play roleA
        When("When calling a role method with union role types")
        Then("There should be no error or exception.")
        And("The result should be correct.")
        val expectedCore = -1
        val expectedRoleA = 0
        val actualCore = matcher.m(someCore)
        val actualRoleA = matcher.m(roleA)

        expectedCore shouldBe actualCore
        expectedRoleA shouldBe actualRoleA
      }
    }
  }
}
