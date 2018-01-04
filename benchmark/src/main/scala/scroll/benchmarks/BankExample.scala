package scroll.benchmarks

import scroll.internal.support.DispatchQuery
import DispatchQuery._
import scroll.internal.Compartment
import scroll.benchmarks.{Currency => Money}
import scroll.internal.graph.CachedScalaRoleGraph

import scala.collection.mutable
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
    val moneyTransfers = mutable.ArrayBuffer.empty[MoneyTransfer]

    def executeTransactions(): Boolean = {
      moneyTransfers.foreach(_.execute())
      true
    }

    class Customer(id: Integer, name: String) {
      val accounts = mutable.ArrayBuffer.empty[Account]

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
      override val plays = new CachedScalaRoleGraph(checkCycles)

      val accounts = players.zipWithIndex.map { case (p, i) =>
        val a = new Account(i, Money(100.0, "USD"))
        (0 until numRoles).foreach(ii => {
          val c = new Customer(ii, "Customer" + i)
          p play c
          c addSavingsAccount a
        })
        a
      }

      (0 until numTransactions).foreach { _ =>
        val transaction = new Transaction {
          override val plays = new CachedScalaRoleGraph(checkCycles)
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
