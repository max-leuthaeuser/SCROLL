package scroll.tests.other

import scroll.internal.formal._
import scroll.tests.AbstractSCROLLTest

class FormalCROMTest extends AbstractSCROLLTest {

  test("Function for mutually disjoint Lists") {
    FormalUtils.mutualDisjoint(List(List.empty, List.empty)) shouldBe true
    FormalUtils.mutualDisjoint(List(List(1, 2), List(3, 4))) shouldBe true
    FormalUtils.mutualDisjoint(List(List(1), List(1))) shouldBe false
    FormalUtils.mutualDisjoint(List(List.empty, List(1), List(1))) shouldBe false
    FormalUtils.mutualDisjoint(List(List(1, 2, 3), List(3, 4, 5))) shouldBe false
    FormalUtils.mutualDisjoint(List(List(1, 1), List(2))) shouldBe true
  }

  test("Function for total function") {
    FormalUtils.totalFunction(List.empty, Map(1 -> List(1))) shouldBe true
    FormalUtils.totalFunction(List(1, 2), Map(1 -> List(1), 2 -> List(2))) shouldBe true
    FormalUtils.totalFunction(List(1, 2), Map(1 -> List(1))) shouldBe false
    FormalUtils.totalFunction(List(1), Map.empty[Int, List[Int]]) shouldBe false
  }

  test("CROM") {
    val test0 = FormalCROM.empty[String, String, String, String]
    val test1 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "3"))
    )
    val test2 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "3"))
    )
    val test3 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4", "5"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3"), "5" -> List.empty),
      Map("a" -> List("2", "3"))
    )
    val test4 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4", "5"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3"), "5" -> List("2")),
      Map("a" -> List("2", "3"))
    )
    val test5 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "2"))
    )
    val test6 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4", "5"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2"), "5" -> List("3")),
      Map("a" -> List("2", "3"))
    )
    val test7 = FormalCROM(
      List("1"),
      List("2", "3", "4"),
      List("5", "6"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("5" -> List("3"), "6" -> List.empty),
      Map("a" -> List("2", "2"))
    )

    val cromtests = Seq(
      (test0, true, true, true, true, true),
      (test1, true, true, true, true, true),
      (test2, false, true, true, true, true),
      (test3, true, false, true, true, true),
      (test4, true, true, false, true, true),
      (test5, true, true, true, false, true),
      (test6, true, true, true, true, false),
      (test7, false, false, false, false, false)
    )

    for { (t, a1, a2, a3, a4, a5) <- cromtests } {
      t.axiom1 shouldBe a1
      t.axiom2 shouldBe a2
      t.axiom3 shouldBe a3
      t.axiom4 shouldBe a4
      t.axiom5 shouldBe a5
      t.wellformed shouldBe (a1 && a2 && a3 && a4 && a5)
    }
  }

  test("CROI") {
    val test1 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "3"))
    )
    val test8 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test8b = FormalCROI.empty[String, String, String, String]
    val test9 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "5", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test10 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "2", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", null), ("3", null)))
    )
    val test11 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4", "5"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4", "5" -> "4"),
      List(("1", "4", "2"), ("1", "5", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test11b = FormalCROI(
      List("1", "5"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4", "5" -> "1"),
      List(("1", "4", "2"), ("5", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test12 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3"), (null, null)))
    )
    val test13 = FormalCROI(
      List("1", "6"),
      List("2", "3", "5"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4", "5" -> "3", "6" -> "1"),
      List(("1", "4", "2"), ("1", "4", "3"), ("6", "4", "5")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test13b = FormalCROI(
      List("1", "6"),
      List("2", "3", "5"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "5" -> "2", "6" -> "1", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3"), ("6", "4", "5")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test14 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3"), ("2", null)))
    )
    val test14b = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3"), (null, "3")))
    )
    val test15 = FormalCROI(
      List("1"),
      List("2", "3", "5", "6"),
      List("4"),
      Map("1" -> "1", "2" -> "5", "3" -> "3", "4" -> "4", "5" -> "3", "6" -> "2"),
      List(("1", "4", "2"), ("1", "4", "5"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3"), (null, null), ("2", null)))
    )

    val croitests = Seq(
      (test8, true, true, true, true, true, true),
      (test8b, true, true, true, true, true, true),
      (test9, false, true, true, true, true, true),
      (test10, true, false, true, true, true, true),
      (test11, true, true, false, true, true, true),
      (test11b, true, true, false, true, true, true),
      (test12, true, true, true, false, true, true),
      (test13, true, true, true, true, false, true),
      (test13b, true, true, true, true, false, true),
      (test14, true, true, true, true, true, false),
      (test14b, true, true, true, true, true, false),
      (test15, false, false, false, false, false, false)
    )

    for { (t, a6, a7, a8, a9, a10, a11) <- croitests } {
      t.axiom6(test1) shouldBe a6
      t.axiom7() shouldBe a7
      t.axiom8() shouldBe a8
      t.axiom9(test1) shouldBe a9
      t.axiom10(test1) shouldBe a10
      t.axiom11(test1) shouldBe a11
      t.compliant(test1) shouldBe (a6 && a7 && a8 && a9 && a10 && a11)
    }
  }

  test("Role Groups") {
    val testrg1 = "2"
    val testrg2 = FormalRoleGroup(List("2", "3"), 2, 2)
    val testrg3 =
      FormalRoleGroup(
        List(FormalRoleGroup(List("2", FormalRoleGroup(List("3"), 1, 2)), 0, 1), "2"),
        1,
        1
      )
    val testrg4 = FormalRoleGroup(List.empty, 0, 0)
    val testrg5 = FormalRoleGroup(List.empty, 1, 1)
    val testrg6 = FormalRoleGroup(List("2"), 0, 0)
    val testrg7 = FormalRoleGroup(List("5"), 0, 0)

    val test8 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )

    val rgtests = Seq(
      (testrg1, List("2"), 1),
      (testrg2, List("2", "3"), 1),
      (testrg3, List("2", "3"), 1),
      (testrg4, List.empty, 1),
      (testrg5, List.empty, 0),
      (testrg6, List("2"), 0),
      (testrg7, List("5"), 1)
    )

    for { (t, s, e) <- rgtests } {
      FormalUtils.atoms(t) shouldBe s
      FormalUtils.evaluate(t, test8, "1", "4") shouldBe e
    }
  }

  test("Constraint Models") {
    val test0 = FormalCROM.empty[String, String, String, String]
    val test1 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "3"))
    )

    val order = (r: List[(String, String)]) => FormalUtils.all(for { (x, y) <- r } yield x <= y)
    val rgxor = FormalRoleGroup(List("2", "3"), 1, 1)

    val testcm0 = FormalConstraintModel.empty[String, String, String, String]
    val testcm1 = FormalConstraintModel.forStrings(
      Map("4" -> List(((1, 3), rgxor))),
      Map("a" -> ((1, 1), (1, 1))),
      List(("a", order))
    )
    val testcm2 =
      FormalConstraintModel.forStrings(
        Map("4" -> List(((1, 1), "2"))),
        Map("a" -> ((1, 1), (1, 1))),
        List.empty
      )
    val testcm3 =
      FormalConstraintModel.forStrings(Map("4" -> List(((1, 1), "2"))), Map.empty, List.empty)
    val testcm4 =
      FormalConstraintModel.forStrings(Map("4" -> List(((1, 1), "5"))), Map.empty, List.empty)
    val testcm5 =
      FormalConstraintModel.forStrings(Map("5" -> List(((1, 1), "2"))), Map.empty, List.empty)

    val cmtests =
      Seq(
        (testcm0, true),
        (testcm1, true),
        (testcm2, true),
        (testcm3, true),
        (testcm4, false),
        (testcm5, true)
      )

    for { (t, a12) <- cmtests } {
      t.axiom12(test1) shouldBe a12
      t.compliant(test1) shouldBe a12
    }

    testcm0.compliant(test0) shouldBe true
  }

  test("Validity") {
    val test0 = FormalCROM.empty[String, String, String, String]
    val test1 = FormalCROM(
      List("1"),
      List("2", "3"),
      List("4"),
      List("a"),
      List(("1", "2"), ("1", "3")),
      Map("4" -> List("2", "3")),
      Map("a" -> List("2", "3"))
    )
    val testcm0 = FormalConstraintModel.empty[String, String, String, String]
    val order   = (r: List[(String, String)]) => FormalUtils.all(for { (x, y) <- r } yield x <= y)
    val rgxor   = FormalRoleGroup(List("2", "3"), 1, 1)
    val testcm1 = FormalConstraintModel.forStrings(
      Map("4" -> List(((1, 3), rgxor))),
      Map("a" -> ((1, 1), (1, 1))),
      List(("a", order))
    )
    val test8 = FormalCROI(
      List("1"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4"),
      List(("1", "4", "2"), ("1", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test8b = FormalCROI.empty[String, String, String, String]

    val test16 = FormalCROI(
      List("1", "5"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4", "5" -> "1"),
      List(("1", "4", "2"), ("5", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test17 = FormalCROI(
      List("1", "0"),
      List("2", "3"),
      List("4"),
      Map("1" -> "1", "2" -> "2", "3" -> "3", "4" -> "4", "0" -> "1"),
      List(("1", "4", "2"), ("0", "4", "3")),
      Map(("a", "4") -> List(("2", "3")))
    )
    val test18 = FormalCROI(
      List("1", "5", "6"),
      List("2", "3", "7"),
      List("4"),
      Map("1" -> "1", "6" -> "1", "2" -> "2", "3" -> "3", "7" -> "3", "4" -> "4", "5" -> "1"),
      List(("1", "4", "2"), ("5", "4", "3"), ("6", "4", "7")),
      Map(("a", "4") -> List(("2", "3"), ("2", "7")))
    )
    val test18b = FormalCROI(
      List("1", "5", "0"),
      List("2", "3", "7"),
      List("4"),
      Map("1" -> "1", "0" -> "1", "2" -> "2", "3" -> "3", "7" -> "2", "4" -> "4", "5" -> "1"),
      List(("1", "4", "2"), ("5", "4", "3"), ("0", "4", "7")),
      Map(("a", "4") -> List(("2", "3"), ("7", "3")))
    )
    val test19 = FormalCROI(
      List("1", "5", "6"),
      List("2", "3", "7", "8"),
      List("4"),
      Map(
        "1" -> "1",
        "5" -> "1",
        "6" -> "1",
        "2" -> "2",
        "3" -> "3",
        "7" -> "2",
        "8" -> "3",
        "4" -> "4"
      ),
      List(("1", "4", "2"), ("5", "4", "3"), ("5", "4", "7"), ("6", "4", "8")),
      Map(("a", "4") -> List(("2", "3"), ("7", "8")))
    )
    val test20 = FormalCROI(
      List("1", "5", "6", "9"),
      List("2", "3", "7", "8"),
      List("4"),
      Map(
        "1" -> "1",
        "5" -> "1",
        "6" -> "1",
        "9" -> "1",
        "2" -> "2",
        "3" -> "3",
        "7" -> "2",
        "8" -> "3",
        "4" -> "4"
      ),
      List(("1", "4", "2"), ("5", "4", "3"), ("6", "4", "7"), ("9", "4", "8")),
      Map(("a", "4") -> List(("2", "3"), ("7", "8")))
    )

    val valtests = Seq(
      (test0, testcm0, test8, false, true, true, true, true),
      (test0, testcm0, test8b, true, true, true, true, true),
      (test1, testcm0, test8, true, true, true, true, true),
      (test1, testcm0, test8b, true, true, true, true, true),
      (test0, testcm1, test8, false, true, false, true, true),
      (test0, testcm1, test8b, true, true, true, true, true),
      (test1, testcm1, test8, true, false, false, true, true),
      (test1, testcm1, test8b, true, true, true, true, true),
      (test1, testcm1, test16, true, true, true, true, true),
      (test1, testcm1, test17, true, true, true, true, false),
      (test1, testcm1, test18, true, true, true, false, true),
      (test1, testcm1, test18b, true, true, true, false, true),
      (test1, testcm1, test19, true, true, false, true, true),
      (test1, testcm1, test20, true, false, true, true, true)
    )

    for { (m, c, i, co, a13, a14, a15, a16) <- valtests } {
      i.compliant(m) shouldBe co
      c.axiom13(m, i) shouldBe a13
      c.axiom14(i) shouldBe a14
      c.axiom15(m, i) shouldBe a15
      c.axiom16(i) shouldBe a16
      c.validity(m, i) shouldBe (co && a13 && a14 && a15 && a16)
    }
  }

}
