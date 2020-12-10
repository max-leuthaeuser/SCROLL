package scroll.tests.parameterized

import scroll.tests.mocks._

class RelationshipTest extends AbstractParameterizedSCROLLTest {

  test("Specifying a relationship") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val p = new CoreA
      new CompartmentUnderTest(c, cc) {
        val rA = new RoleA
        val rB = new RoleB
        val rC = new RoleC
        p play rA play rB
        val relSize = 1
        val rel1 = roleRelationships.create("rel1").from[RoleA](relSize).to[RoleB](relSize)
        rel1.left() should contain only rA
        rel1.right() should contain only rB
        val rel2Name = "rel2"
        val rel2 = roleRelationships.create(rel2Name).from[RoleA](relSize).to[RoleC](relSize)
        rel2.left() should contain only rA
        the[AssertionError] thrownBy {
          rel2.right()
        } should have message s"assertion failed: With a concrete multiplicity for '$rel2Name' of '$relSize' the resulting role set should have the same size!"

        import scroll.internal.util.Many._

        val rel3 = roleRelationships.create("rel3").from[RoleA](1).to[RoleB](*)
        rel3.left() should contain only rA
        rel3.right() should contain only rB
        val rB2 = new RoleB
        p play rB2
        rel3.right() should contain theSameElementsAs Set(rB, rB2)
        val rB3 = new RoleB
        p play rB3
        rel3.right() should contain theSameElementsAs Set(rB, rB2, rB3)
      }
    }
  }

}
