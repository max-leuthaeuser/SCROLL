package scroll.examples

import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery.Bypassing
import scroll.internal.util.Log.info

object AnotherBankExample extends App {

  // Naturals
  case class Person(name: String)

  case class Company(name: String)

  type Money = Double

  case class Account(var balance: Money = 0) {
    def increase(amount: Money): Unit = {
      balance = balance + amount
    }

    def decrease(amount: Money): Unit = {
      balance = balance - amount
    }
  }

  class Bank extends Compartment {

    case class Consultant(phone: String)

    case class Customer(id: String)

    case class CheckingsAccount(limit: Money) {
      def increase(amount: Money): Unit = {
        if (amount > limit) info("Limit reached in increase!")
        // so we won't calling decrease() recursively on this:
        implicit val dd = Bypassing(_.isInstanceOf[CheckingsAccount])
        val _ = (+this).increase(Math.min(amount, limit))

      }

      def decrease(amount: Money): Unit = {
        if (amount > limit) info("Limit reached in decrease!")
        // so we won't calling decrease() recursively on this:
        implicit val dd = Bypassing(_.isInstanceOf[CheckingsAccount])
        val _ = (+this).decrease(Math.min(amount, limit))
      }
    }

    case class Source()

    case class Target()

    case class Owns(left: Customer, right: Set[CheckingsAccount])

    case class Transfer(left: Source, right: Target) {
      var amount: Money = 0
      var creation: String = "now"
    }

  }

  // Instance level
  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = Account(10.0)
  val accForBrian = Account(0)

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
