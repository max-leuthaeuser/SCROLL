package scroll.tests.parameterized

import scroll.tests.mocks._

class FacetsTest extends AbstractParameterizedSCROLLTest {

  object TestFacet extends Enumeration {
    type Color = Value
    val Red, Blue, Green = Value
  }

  import TestFacet._

  test("Adding facets") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val player = someCore <+> Red
        player.hasFacets(Red) shouldBe true
        player.facets() shouldBe Seq(Red)
      }
    }
  }

  test("Removing facets") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCore = new CoreA()
      new CompartmentUnderTest(c, cc) {
        val player = someCore <+> Red
        player.drop(Red)
        player.hasFacets(Red) shouldBe false
        player.facets() shouldBe empty
      }
    }
  }

  test("Transferring facets") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA = new CoreA()
      val someCoreB = new CoreB()
      new CompartmentUnderTest(c, cc) {
        val playerA = someCoreA <+> Red
        val playerB = +someCoreB
        someCoreA transfer Red to someCoreB
        playerA.hasFacets(Red) shouldBe false
        playerB.hasFacets(Red) shouldBe true
      }
    }
  }

  test("Filtering for facets") {
    forAll(PARAMS) { (c: Boolean, cc: Boolean) =>
      val someCoreA1 = new CoreA()
      val someCoreA2 = new CoreA()
      val someCoreA3 = new CoreA()
      val someCoreA4 = new CoreA()
      val someCoreA5 = new CoreA()
      val someCoreA6 = new CoreA()
      new CompartmentUnderTest(c, cc) {
        someCoreA1 <+> Red
        someCoreA2 <+> Red
        someCoreA3 <+> Red
        someCoreA4 <+> Blue
        someCoreA5 <+> Blue
        someCoreA6 <+> Blue

        roleQueries.all { (c: CoreA) =>
          c.hasFacets(Red)
        } should contain theSameElementsAs Set(someCoreA1, someCoreA2, someCoreA3)

        roleQueries.all { (c: CoreA) =>
          c.hasSomeFacet(Red)
        } should contain theSameElementsAs Set(someCoreA1, someCoreA2, someCoreA3)

        roleQueries.all { (c: CoreA) =>
          c.hasFacets(Blue)
        } should contain theSameElementsAs Set(someCoreA4, someCoreA5, someCoreA6)

        roleQueries.all { (c: CoreA) =>
          c.hasSomeFacet(Blue)
        } should contain theSameElementsAs Set(someCoreA4, someCoreA5, someCoreA6)

        roleQueries.all { (c: CoreA) =>
          c.hasSomeFacet(Red, Blue)
        } should contain theSameElementsAs Set(someCoreA1, someCoreA2, someCoreA3, someCoreA4, someCoreA5, someCoreA6)

        roleQueries.all { (c: CoreA) =>
          c.hasSomeFacet(Green)
        } shouldBe empty

        roleQueries.all { (c: CoreA) =>
          c.hasFacets(Green)
        } shouldBe empty

        roleQueries.all { (c: CoreA) =>
          c.hasFacets(Red, Blue)
        } shouldBe empty

        roleQueries.all { (c: CoreA) =>
          c.hasFacets(Red, Blue, Green)
        } shouldBe empty

      }
    }
  }

}
