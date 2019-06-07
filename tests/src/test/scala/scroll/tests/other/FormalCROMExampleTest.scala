package scroll.tests.other

import scroll.internal.formal._
import scroll.tests.AbstractSCROLLTest

class FormalCROMExampleTest extends AbstractSCROLLTest {

  private val bank = FormalCROM(
    List("Person", "Company", "Account"),
    List("Customer", "Consultant", "CA", "SA", "Source", "Target", "MoneyTransfer"),
    List("Bank", "Transaction"),
    List("own_ca", "own_sa", "advises", "trans"),
    List(("Person", "Consultant"), ("Person", "Customer"), ("Company", "Customer"),
      ("Account", "Source"), ("Account", "Target"), ("Account", "CA"), ("Account", "SA"),
      ("Transaction", "MoneyTransfer")),
    Map("Bank" -> List("Consultant", "Customer", "CA", "SA", "MoneyTransfer"),
      "Transaction" -> List("Source", "Target")),
    Map("own_ca" -> List("Customer", "CA"),
      "own_sa" -> List("Customer", "SA"),
      "advises" -> List("Consultant", "Customer"),
      "trans" -> List("Source", "Target"))
  )

  test("Well-formedness of banking model") {
    bank.wellformed shouldBe true
  }

  private val bankaccounts = FormalRoleGroup(List("CA", "SA"), 1, 1)
  private val participants = FormalRoleGroup(List("Source", "Target"), 1, 1)

  private val irreflexive = (r: List[(String, String)]) => !FormalUtils.any(for {(x, y) <- r} yield x == y)

  private val inf = Integer.MAX_VALUE

  private val c_bank = FormalConstraintModel.forStrings(
    Map("Bank" -> List(((1, inf), "Consultant"), ((0, inf), bankaccounts)),
      "Transaction" -> List(((2, 2), participants))),
    Map("own_ca" -> ((1, 1), (0, inf)),
      "own_sa" -> ((1, inf), (0, inf)),
      "advises" -> ((0, inf), (1, inf)),
      "trans" -> ((1, 1), (1, 1))),
    List(("advises", irreflexive))
  )

  test("Compliance of banking model") {
    c_bank.compliant(bank) shouldBe true
  }

  private val bank1 = FormalCROI(
    List("Peter", "Klaus", "Google", "Account_1", "Account_2"),
    List("Cu_1", "Cu_2", "Cu_3", "Ca", "Sa", "S", "T", "M"),
    List("bank", "transaction"),
    Map("Peter" -> "Person", "Klaus" -> "Person", "Google" -> "Company",
      "Account_1" -> "Account", "Account_2" -> "Account",
      "Cu_1" -> "Customer", "Cu_2" -> "Customer", "Cu_3" -> "Customer",
      "Ca" -> "CA", "Sa" -> "SA", "S" -> "Source", "T" -> "Target",
      "M" -> "MoneyTransfer",
      "bank" -> "Bank", "transaction" -> "Transaction"),
    List(("Klaus", "bank", "Cu_1"), ("Google", "bank", "Cu_2"), ("Peter", "bank", "Cu_3"),
      ("Account_2", "bank", "Ca"), ("Account_1", "bank", "Sa"),
      ("transaction", "bank", "M"),
      ("Account_2", "transaction", "S"), ("Account_2", "transaction", "T")),
    Map(("own_ca", "bank") -> List(("Cu_1", "Ca"), ("Cu_2", null), ("Cu_3", null)),
      ("own_sa", "bank") -> List(("Cu_1", null), ("Cu_2", "Sa"), ("Cu_3", null)),
      ("advises", "bank") -> List((null, "Cu_1"), (null, "Cu_2"), (null, "Cu_3")),
      ("trans", "transaction") -> List(("S", "T")))
  )

  test("Axioms 6 - 11 of the banking example 1") {
    bank1.compliant(bank) shouldBe true
    bank1.axiom6(bank) shouldBe true
    bank1.axiom7() shouldBe true
    bank1.axiom8() shouldBe true
    bank1.axiom9(bank) shouldBe true
    bank1.axiom10(bank) shouldBe true
    bank1.axiom11(bank) shouldBe true
  }

  val bank2 = FormalCROI(
    List("Peter", "Klaus", "Google", "Account_1", "Account_2"),
    List("Con", "Cu_1", "Cu_2", "Ca", "Sa", "S", "T", "M"),
    List("bank", "transaction"),
    Map("Peter" -> "Person", "Klaus" -> "Person", "Google" -> "Company",
      "Account_1" -> "Account", "Account_2" -> "Account",
      "Con" -> "Consultant", "Cu_1" -> "Customer", "Cu_2" -> "Customer",
      "Ca" -> "CA", "Sa" -> "SA", "S" -> "Source", "T" -> "Target",
      "M" -> "MoneyTransfer",
      "bank" -> "Bank", "transaction" -> "Transaction"),
    List(("Klaus", "bank", "Cu_1"), ("Google", "bank", "Cu_2"), ("Peter", "bank", "Con"),
      ("Account_2", "bank", "Ca"), ("Account_1", "bank", "Sa"),
      ("transaction", "bank", "M"),
      ("Account_1", "transaction", "S"), ("Account_2", "transaction", "T")),
    Map(("own_ca", "bank") -> List(("Cu_1", "Ca"), ("Cu_2", null)),
      ("own_sa", "bank") -> List(("Cu_1", null), ("Cu_2", "Sa")),
      ("advises", "bank") -> List((null, "Cu_1"), ("Con", "Cu_2")),
      ("trans", "transaction") -> List(("S", "T")))
  )

  test("Axioms 6 - 11 of the banking example 2") {
    bank2.compliant(bank) shouldBe true
    bank2.axiom6(bank) shouldBe true
    bank2.axiom7() shouldBe true
    bank2.axiom8() shouldBe true
    bank2.axiom9(bank) shouldBe true
    bank2.axiom10(bank) shouldBe true
    bank2.axiom11(bank) shouldBe true
  }

  test("Validity of the banking example 1") {
    c_bank.validity(bank, bank1) shouldBe false
    c_bank.axiom13(bank, bank1) shouldBe false
    c_bank.axiom14(bank1) shouldBe false
    c_bank.axiom15(bank, bank1) shouldBe false
    c_bank.axiom16(bank1) shouldBe true
  }

  test("Validity of the banking example 2") {
    c_bank.validity(bank, bank2) shouldBe true
    c_bank.axiom13(bank, bank2) shouldBe true
    c_bank.axiom14(bank2) shouldBe true
    c_bank.axiom15(bank, bank2) shouldBe true
    c_bank.axiom16(bank2) shouldBe true
  }

}
