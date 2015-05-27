package examples

import internal.Compartment
import internal.DispatchQuery._
import annotations.Role
import internal.util.Log.info

object AnotherBankExample extends App {

  // Naturals
  case class Person(name: String)

  case class Company(name: String)

  type Money = Double

  case class Account(var balance: Money = 0) {
    def increase(amount: Money) {
      balance = balance + amount
    }

    def decrease(amount: Money) {
      balance = balance - amount
    }
  }

  class Bank extends Compartment {

    @Role case class Consultant(phone: String)

    @Role case class Customer(id: String)

    @Role case class CheckingsAccount(limit: Money) {
      def increase(amount: Money) {
        if (amount > limit) info("Limit reached in increase!")
        implicit val dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[CheckingsAccount]).
          Through(anything).
          // so we won't calling decrease() recursively on this
          Bypassing(_.isInstanceOf[CheckingsAccount])
        (+this).increase(Math.min(amount, limit))

      }

      def decrease(amount: Money) {
        if (amount > limit) info("Limit reached in decrease!")
        implicit val dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[CheckingsAccount]).
          Through(anything).
          // so we won't calling decrease() recursively on this
          Bypassing(_.isInstanceOf[CheckingsAccount])
        (+this).decrease(Math.min(amount, limit))
      }
    }

    @Role case class Source()

    @Role case class Target()

    case class Owns(left: Customer, right: Set[CheckingsAccount])

    case class Transfer(left: Source, right: Target) {
      var amount: Money = 0
      var creation: String = "now"
    }

  }

  // Instance level
  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = new Account(10.0)
  val accForBrian = new Account(0)

  new Bank {
    val c1 = Customer("001")
    val c2 = Customer("002")

    val a1 = CheckingsAccount(5)
    val a2 = CheckingsAccount(10)

    stan play c1
    brian play c2
    accForStan play a1
    accForBrian play a2

    val acc001 = Owns(c1, Set(a1))
    val acc002 = Owns(c2, Set(a2))

    a1 play Source()
    a2 play Target()

    val someTransfer = Transfer(one[Source](), one[Target]())
    someTransfer.amount = 10.0

    (+someTransfer.left) decrease someTransfer.amount
    (+someTransfer.right) increase someTransfer.amount

    info("Balance: " + accForStan.balance)
    info("Balance: " + accForBrian.balance)
  }
}
