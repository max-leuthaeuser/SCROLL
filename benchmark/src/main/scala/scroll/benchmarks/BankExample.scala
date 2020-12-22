package scroll.benchmarks

import scroll.internal.compartment.impl.Compartment
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery.Bypassing

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class BankExample {

  class Person(val name: String)

  class Account(id: Integer, var balance: Double) {

    def increase(amount: Double): Unit = {
      balance = balance + amount
    }

    def decrease(amount: Double): Unit = {
      balance = balance - amount
    }
  }

  trait Transaction extends Compartment {
    var amount: Double = _

    var from: Source = _
    var to: Target = _

    def execute(): Unit = {
      from.withdraw(amount)
      to.deposite(amount)
    }

    class Source() {
      def withdraw(amount: Double): Unit = {
        val _ = (+this).decrease(amount)
      }
    }

    class Target() {
      def deposite(amount: Double): Unit = {
        val _ = (+this).increase(amount)
      }
    }

  }

  trait Bank extends Compartment {
    protected val moneyTransfers: ArrayBuffer[MoneyTransfer] = ArrayBuffer.empty[MoneyTransfer]

    def executeTransactions(): Boolean = {
      moneyTransfers.foreach(_.execute())
      true
    }

    class Customer(name: String) {
      private var account: Account = _

      def setSavingsAccount(a: Account): Unit = {
        val sa = new SavingsAccount()
        account = a
        val _ = a play sa
      }
    }

    class MoneyTransfer() {
      def execute(): Unit = {
        given DispatchQuery = Bypassing(_.isInstanceOf[MoneyTransfer])
        val _ = (+this).execute()
      }
    }

    class SavingsAccount() {
      private val transactionFee: Double = 0.1

      def decrease(amount: Double): Unit = {
        given DispatchQuery = Bypassing(_.isInstanceOf[SavingsAccount])
        val _ = (+this).decrease(amount + amount * transactionFee)
      }
    }

  }

  var bank: Bank = _

  def build(numPlayer: Int, numRoles: Int, numTransactions: Int, cached: Boolean, checkCycles: Boolean = false): BankExample = {
    val players = (0 until numPlayer).map(i => new Person("Name-" + i))

    bank = new Bank {
      roleGraph.reconfigure(cached, checkCycles)

      private val accounts: Seq[Account] = players.map { p =>
        val a = new Account(p.name.hashCode, 100.0)
        val roles = (0 until numRoles).map(ii => {
          val c = new Customer(s"Customer-$ii-${p.name}")
          c setSavingsAccount a
          c
        })
        p play roles.head
        roles.sliding(2).foreach(l => l(0) play l(1))
        a
      }

      (0 until numTransactions).foreach { _ =>
        val transaction: Transaction = new Transaction {
          roleGraph.reconfigure(cached, checkCycles)

          amount = 10
          from = new Source()
          to = new Target()
          accounts(Random.nextInt(accounts.size)) play from
          accounts(Random.nextInt(accounts.size)) play to
        }
        val mt = new MoneyTransfer()
        transaction play mt
        moneyTransfers.append(mt)
        compartmentRelations.combine(transaction)
      }
    }
    this
  }

}
