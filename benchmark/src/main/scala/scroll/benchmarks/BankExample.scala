package scroll.benchmarks

import scroll.internal.support.DispatchQuery
import DispatchQuery._
import scroll.internal.Compartment
import scroll.benchmarks.{Currency => Money}
import scroll.internal.graph.CachedScalaRoleGraph

import scala.collection.mutable
import scala.util.Random

class BankExample {

  case class Person(title: String, firstName: String, lastName: String, address: String)

  case class Account(var balance: Money, id: Integer) {

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

    def execute(): Boolean = {
      from.withdraw(amount)
      to.deposite(amount)
      true
    }

    case class Source() {
      def withdraw(amount: Money): Unit = {
        val _ = +this decrease amount
      }
    }

    case class Target() {
      def deposite(amount: Money): Unit = {
        val _ = +this increase amount
      }
    }

  }

  trait Bank extends Compartment {
    var moneyTransfers = mutable.ListBuffer.empty[MoneyTransfer]

    def executeTransactions(): Boolean = moneyTransfers.forall(_.execute())

    case class Customer(name: String, id: Integer) {
      var accounts = mutable.ArrayBuffer.empty[Account]

      def addSavingsAccount(a: Account): Boolean = {
        val sa = SavingsAccount(0.1)
        accounts.append(a)
        a play sa
        true
      }

      def addCheckingsAccount(a: Account): Boolean = {
        val ca = CheckingsAccount(Money(100, "USD"))
        accounts.append(a)
        a play ca
        true
      }
    }

    case class MoneyTransfer() {
      def execute(): Boolean = {
        implicit val dd = Bypassing(_.isInstanceOf[MoneyTransfer])
        +this execute()
        true
      }
    }

    case class CheckingsAccount(var limit: Money) {
      def decrease(amount: Money): Unit = amount match {
        case a if a <= limit =>
          implicit val dd = Bypassing(_.isInstanceOf[CheckingsAccount])
          val _ = +this decrease amount
        case _ => throw new IllegalArgumentException("Amount > limit!")
      }
    }

    case class SavingsAccount(var transactionFee: Double) {
      def decrease(amount: Money): Unit = {
        implicit val dd = Bypassing(_.isInstanceOf[SavingsAccount])
        //println("dec from SA")
        val _ = +this decrease (amount + amount * transactionFee)
      }
    }

  }

  var bank: Bank = _

  def build(numPlayer: Int, numRoles: Int, numTransactions: Int, checkCycles: Boolean = false): BankExample = {
    val players = (0 until numPlayer).map(i => Person("Mr.", "Stan", "Mejer" + i, "Fake Street 1A"))

    bank = new Bank {
      override val plays = new CachedScalaRoleGraph(checkCycles)

      val accounts = players.zipWithIndex.map { case (p, i) =>
        val a = Account(Money(100.0, "USD"), i)
        (0 until numRoles).map(ii => {
          val c = Customer("Customer" + i, ii)
          p play c
          c addSavingsAccount a
        })
        a
      }

      (0 until numTransactions).foreach { _ =>
        val transaction = new Transaction {
          override val plays = new CachedScalaRoleGraph(checkCycles)
          amount = Money(10.0, "USD")
          from = Source()
          to = Target()
          accounts(Random.nextInt(accounts.size)) play from
          accounts(Random.nextInt(accounts.size)) play to
        }
        val mt = MoneyTransfer()
        transaction play mt
        moneyTransfers.append(mt)
        transaction partOf this
      }
    }
    this
  }

  def benchmark(): Boolean = bank.executeTransactions()

}