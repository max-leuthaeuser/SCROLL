package scroll.examples

import scroll.examples.currency.{Currency => Money}
import scroll.internal.compartment.impl.Compartment
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery.Bypassing
import scroll.internal.util.Many._

import scala.collection.mutable

object BankExample {

  case class Person(name: String)

  class Account(var balance: Money = Money(0, "USD")) {

    def increase(amount: Money): Unit = {
      balance = balance + amount
    }

    def decrease(amount: Money): Unit = {
      balance = balance - amount
    }
  }

  class Bank extends Compartment {

    roleGroups.create("Accounts").containing[CheckingsAccount, SavingsAccount](1, 1)(0, *)

    class Customer() {
      private val checkingsAccounts = mutable.ArrayBuffer[CheckingsAccount]()
      private val savingsAccounts = mutable.ArrayBuffer[SavingsAccount]()

      def addCheckingsAccount(acc: CheckingsAccount): Unit = checkingsAccounts += acc

      def addSavingsAccount(acc: SavingsAccount): Unit = savingsAccounts += acc

      def listBalances(): Unit = {
        checkingsAccounts.foreach(a => println("CheckingsAccount: " + a + " -> " + (+a).balance))
        savingsAccounts.foreach(a => println("SavingsAccount: " + a + " -> " + (+a).balance))
      }
    }

    class CheckingsAccount() {
      def decrease(amount: Money): Unit = {
        dd = Bypassing(_.isInstanceOf[CheckingsAccount])
        val _ = (+this).decrease(amount)
      }
    }

    class SavingsAccount() {

      private val transactionFee = 0.1

      private def calcTransactionFee(amount: Money): Money = amount * transactionFee

      def increase(amount: Money): Unit = {
        println("Increasing with fee.")
        dd = Bypassing(_.isInstanceOf[SavingsAccount])
        val _ = (+this).increase(amount - calcTransactionFee(amount))
      }
    }

    class TransactionRole() {
      def execute(): Unit = {
        println("Executing from Role.")
        dd = Bypassing(_.isInstanceOf[TransactionRole])
        val _ = (+this).execute()
      }
    }

  }

  class Transaction(val amount: Money) extends Compartment {

    roleGroups.create("Transaction").containing[Source, Target](1, 1)(2, 2)

    private val transferRel = roleRelationships.create("transfer").from[Source](1).to[Target](1)

    def execute(): Unit = {
      println("Executing from Player.")
      roleQueries.one[Source]().withDraw(amount)
      roleQueries.one[Target]().deposit(amount)
      val from = transferRel.left().head
      val to = transferRel.right().head
      println(s"Transferred '$amount' from '$from' to '$to'.")
    }

    class Source() {
      def withDraw(m: Money): Unit = {
        val _ = (+this).decrease(m)
      }
    }

    class Target() {
      def deposit(m: Money): Unit = {
        val _ = (+this).increase(m)
      }
    }

  }

  implicit var dd: DispatchQuery = DispatchQuery.empty

  def main(args: Array[String]): Unit = {
    val stan = Person("Stan")
    val brian = Person("Brian")

    val accForStan = new Account(Money(10.0, "USD"))
    val accForBrian = new Account(Money(0, "USD"))

    val _ = new Bank {
      val ca = new CheckingsAccount()
      val sa = new SavingsAccount()

      roleGroups.checked {
        accForStan play ca
        accForBrian play sa
      }
      stan play new Customer()
      brian play new Customer()

      (+stan).addCheckingsAccount(ca)
      (+brian).addSavingsAccount(sa)

      println("### Before transaction ###")
      println("Balance for Stan:")
      (+stan).listBalances()
      println("Balance for Brian:")
      (+brian).listBalances()

      private val transaction = new Transaction(Money(10.0, "USD")) {
        roleGroups.checked {
          accForStan play new Source()
          accForBrian play new Target()
        }
      }

      transaction.compartmentRelations.partOf(this)

      (transaction play new TransactionRole()).execute()

      println("### After transaction ###")
      println("Balance for Stan:")
      (+stan).listBalances()
      println("Balance for Brian:")
      (+brian).listBalances()
    }
  }
}
