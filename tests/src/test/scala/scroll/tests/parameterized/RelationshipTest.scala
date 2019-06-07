package scroll.tests.parameterized

import scroll.tests.mocks._

class RelationshipTest extends AbstractParameterizedSCROLLTest {

  test("Specifying a relationship") {
    forAll("cached", "checkForCycles") { (c: Boolean, cc: Boolean) =>
      val p = new CoreA
      new CompartmentUnderTest(c, cc) {
        val rA = new RoleA
        val rB = new RoleB
        val rC = new RoleC
        p play rA play rB
        val rel1 = Relationship("rel1").from[RoleA](1).to[RoleB](1)
        rel1.left() should contain only rA
        rel1.right() should contain only rB
        val rel2 = Relationship("rel2").from[RoleA](1).to[RoleC](1)
        rel2.left() should contain only rA
        a[AssertionError] should be thrownBy {
          rel2.right()
        }

        import scroll.internal.util.Many._

        val rel3 = Relationship("rel3").from[RoleA](1).to[RoleB](*)
        rel3.left() should contain only rA
        rel3.right() should contain only rB
        val rB2 = new RoleB
        p play rB2
        rel3.right() should contain only(rB, rB2)
        val rB3 = new RoleB
        p play rB3
        rel3.right() should contain only(rB, rB2, rB3)
      } shouldNot be(null)
    }
  }

}
