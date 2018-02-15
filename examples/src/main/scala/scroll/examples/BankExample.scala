package scroll.examples

import scroll.examples.currency.{Currency => Money}
import scroll.internal.support.DispatchQuery.Bypassing
import scroll.internal.util.Log.info
import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery
import scroll.internal.util.Many._

import scala.collection.mutable

object BankExample extends App {

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

    RoleGroup("Accounts").containing[CheckingsAccount, SavingsAccount](1, 1)(0, *)

    class Customer() {
      private val checkingsAccounts = mutable.ArrayBuffer[CheckingsAccount]()
      private val savingsAccounts = mutable.ArrayBuffer[SavingsAccount]()

      def addCheckingsAccount(acc: CheckingsAccount): Unit = checkingsAccounts += acc

      def addSavingsAccount(acc: SavingsAccount): Unit = savingsAccounts += acc

      def listBalances(): Unit = {
        checkingsAccounts.foreach(a => info("CheckingsAccount: " + a + " -> " + (+a).balance))
        savingsAccounts.foreach(a => info("SavingsAccount: " + a + " -> " + (+a).balance))
      }
    }

    class CheckingsAccount() {
      def decrease(amount: Money): Unit = {
        dd = Bypassing(_.isInstanceOf[CheckingsAccount])
        val _ = +this decrease amount
      }
    }

    class SavingsAccount() {

      private val transactionFee = 0.1

      private def calcTransactionFee(amount: Money): Money = amount * transactionFee

      def increase(amount: Money): Unit = {
        info("Increasing with fee.")
        dd = Bypassing(_.isInstanceOf[SavingsAccount])
        val _ = +this increase (amount - calcTransactionFee(amount))
      }
    }

    class TransactionRole() {
      def execute(): Unit = {
        info("Executing from Role.")
        dd = Bypassing(_.isInstanceOf[TransactionRole])
        val _ = +this execute()
      }
    }

  }

  class Transaction(val amount: Money) extends Compartment {

    RoleGroup("Transaction").containing[Source, Target](1, 1)(2, 2)

    private val transferRel = Relationship("transfer").from[Source](1).to[Target](1)

    def execute(): Unit = {
      info("Executing from Player.")
      one[Source]().withDraw(amount)
      one[Target]().deposit(amount)
      val from = transferRel.left().head
      val to = transferRel.right().head
      info(s"Transferred '$amount' from '$from' to '$to'.")
    }

    class Source() {
      def withDraw(m: Money): Unit = {
        val _ = +this decrease m
      }
    }

    class Target() {
      def deposit(m: Money): Unit = {
        val _ = +this increase m
      }
    }

  }

  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = new Account(Money(10.0, "USD"))
  val accForBrian = new Account(Money(0, "USD"))

  implicit var dd: DispatchQuery = DispatchQuery.empty

  new Bank {
    val ca = new CheckingsAccount
    val sa = new SavingsAccount

    RoleGroupsChecked {
      accForStan play ca
      accForBrian play sa
    }
    stan play new Customer
    brian play new Customer

    +stan addCheckingsAccount ca
    +brian addSavingsAccount sa

    info("### Before transaction ###")
    info("Balance for Stan:")
    +stan listBalances()
    info("Balance for Brian:")
    +brian listBalances()

    private val transaction = new Transaction(Money(10.0, "USD")) {
      RoleGroupsChecked {
        accForStan play new Source
        accForBrian play new Target
      }
    }

    transaction partOf this

    transaction play new TransactionRole execute()

    info("### After transaction ###")
    info("Balance for Stan:")
    +stan listBalances()
    info("Balance for Brian:")
    +brian listBalances()
  }
}
