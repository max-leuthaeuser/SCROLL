package benchmarks.models

import annotations.Role
import scroll.internal.Compartment

object SmallBank {

  class Account(custId: Int, name: String)

  class Bank extends Compartment {

    val transfer = Relationship("transfer").from[CheckingAccount](1).to[SavingsAccount](1)

    @Role
    class CheckingAccount(balance: Float)

    @Role
    class SavingsAccount(balance: Float)

  }

}
