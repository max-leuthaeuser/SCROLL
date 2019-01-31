package scroll.benchmarks

import scroll.internal.support.DispatchQuery
import DispatchQuery._
import scroll.internal.Compartment
import scroll.benchmarks.{Currency => Money}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class BankExample {

  class Person(title: String, firstName: String, lastName: String, address: String)

  class Account(id: Integer, var balance: Money) {

    def increase(amount: Money): Unit = {
      balance = balance + amount
    }

    def decrease(amount: Money): Unit = {
      balance = balance - amount
    }
  }

  trait Transaction extends Compartment {
    var amount: Money = _

    var from: Source = _
    var to: Target = _

    def execute(): Unit = {
      from.withdraw(amount)
      to.deposite(amount)
    }

    class Source() {
      def withdraw(amount: Money): Unit = {
        val _ = +this decrease amount
      }
    }

    class Target() {
      def deposite(amount: Money): Unit = {
        val _ = +this increase amount
      }
    }

  }

  trait Bank extends Compartment {
    protected val moneyTransfers: ArrayBuffer[MoneyTransfer] = ArrayBuffer.empty[MoneyTransfer]

    def executeTransactions(): Boolean = {
      moneyTransfers.foreach(_.execute())
      true
    }

    class Customer(id: Integer, name: String) {
      private val accounts = ArrayBuffer.empty[Account]

      def addSavingsAccount(a: Account): Unit = {
        val sa = new SavingsAccount()
        accounts.append(a)
        val _ = a play sa
      }

      def addCheckingsAccount(a: Account): Unit = {
        val ca = new CheckingsAccount()
        accounts.append(a)
        val _ = a play ca
      }
    }

    class MoneyTransfer() {
      def execute(): Unit = {
        implicit val dd = Bypassing(_.isInstanceOf[MoneyTransfer])
        val _ = +this execute()
      }
    }

    class CheckingsAccount() {
      private val limit: Money = Money(100, "USD")

      def decrease(amount: Money): Unit = amount match {
        case a if a <= limit =>
          implicit val dd = Bypassing(_.isInstanceOf[CheckingsAccount])
          val _ = +this decrease amount
        case _ => throw new IllegalArgumentException("Amount > limit!")
      }
    }

    class SavingsAccount() {
      private val transactionFee: Double = 0.1

      def decrease(amount: Money): Unit = {
        implicit val dd = Bypassing(_.isInstanceOf[SavingsAccount])
        val _ = +this decrease (amount + amount * transactionFee)
      }
    }

  }

  var bank: Bank = _

  def build(numPlayer: Int, numRoles: Int, numTransactions: Int, checkCycles: Boolean = false): BankExample = {
    val players = (0 until numPlayer).map(i => new Person("Mr.", "Stan", "Mejer" + i, "Fake Street 1A"))

    bank = new Bank {
      reconfigure(cached = true, checkCycles)

      private val accounts = players.zipWithIndex.map { case (p, i) =>
        val a = new Account(i, Money(100.0, "USD"))
        val roles = (0 until numRoles).map(ii => {
          val c = new Customer(ii, "Customer" + i)
          c addSavingsAccount a
          c
        })
        p play roles.head
        roles.sliding(2).foreach(l => l(0) play l(1))
        a
      }

      (0 until numTransactions).foreach { _ =>
        val transaction = new Transaction {
          reconfigure(cached = true, checkCycles)

          amount = Money(10.0, "USD")
          from = new Source()
          to = new Target()
          accounts(Random.nextInt(accounts.size)) play from
          accounts(Random.nextInt(accounts.size)) play to
        }
        val mt = new MoneyTransfer()
        transaction play mt
        moneyTransfers.append(mt)
        transaction partOf this
      }
    }
    this
  }

  def benchmark(): Boolean = bank.executeTransactions()

}
