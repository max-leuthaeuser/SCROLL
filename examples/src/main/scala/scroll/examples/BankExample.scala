package scroll.examples

import scroll.internal.compartment.impl.Compartment
import scroll.internal.dispatch.DispatchQuery
import scroll.internal.dispatch.DispatchQuery.Bypassing
import scroll.internal.util.Many.*

object BankExample {

  @main def runBankExample(): Unit = {
    val stan  = Person("Stan")
    val brian = Person("Brian")

    val accForStan  = new Account(10)
    val accForBrian = new Account(0)

    val _ = new Bank {
      val ca = new CheckingsAccount
      val sa = new SavingsAccount

      roleGroups.checked {
        accForStan play ca
        accForBrian play sa
      }
      stan play new Customer
      brian play new Customer

      (+stan).addCheckingsAccount(ca)
      (+brian).addSavingsAccount(sa)

      println("### Before transaction ###")
      println("Balance for Stan:")
      (+stan).listBalances()
      println("Balance for Brian:")
      (+brian).listBalances()

      private val transaction = new Transaction(10) {
        roleGroups.checked {
          accForStan play new Source
          accForBrian play new Target
        }
      }

      transaction.compartmentRelations.partOf(this)

      (transaction play new TransactionRole).execute()

      println("### After transaction ###")
      println("Balance for Stan:")
      (+stan).listBalances()
      println("Balance for Brian:")
      (+brian).listBalances()
    }
  }

  case class Person(name: String)

  class Account(var balance: Double = 0) {

    def increase(amount: Double): Unit = balance = balance + amount

    def decrease(amount: Double): Unit = balance = balance - amount
  }

  class Bank extends Compartment {

    roleGroups.create("Accounts").containing[CheckingsAccount, SavingsAccount](1, 1)(0, *)

    class Customer() {
      private val checkingsAccounts = scala.collection.mutable.ArrayBuffer[CheckingsAccount]()
      private val savingsAccounts   = scala.collection.mutable.ArrayBuffer[SavingsAccount]()

      def addCheckingsAccount(acc: CheckingsAccount): Unit = checkingsAccounts += acc

      def addSavingsAccount(acc: SavingsAccount): Unit = savingsAccounts += acc

      def listBalances(): Unit = {
        checkingsAccounts.foreach { a =>
          val account         = a
          val balance: Double = (+account).balance
          println(s"CheckingsAccount '$account': $balance")
        }
        savingsAccounts.foreach { a =>
          val account         = a
          val balance: Double = (+account).balance
          println(s"SavingsAccount '$account': $balance")
        }
      }

    }

    class CheckingsAccount() {

      def decrease(amount: Double): Unit = {
        given DispatchQuery = Bypassing(_.isInstanceOf[CheckingsAccount])
        val _               = (+this).decrease(amount)
      }

    }

    class SavingsAccount() {

      private val transactionFee = 0.1

      def increase(amount: Double): Unit = {
        println("Increasing with fee.")
        given DispatchQuery = Bypassing(_.isInstanceOf[SavingsAccount])
        val _               = (+this).increase(amount - calcTransactionFee(amount))
      }

      private def calcTransactionFee(amount: Double): Double = amount * transactionFee
    }

    class TransactionRole() {

      def execute(): Unit = {
        println("Executing from Role.")
        given DispatchQuery = Bypassing(_.isInstanceOf[TransactionRole])
        val _               = (+this).execute()
      }

    }

  }

  class Transaction(val amount: Double) extends Compartment {

    roleGroups.create("Transaction").containing[Source, Target](1, 1)(2, 2)

    private val transferRel = roleRelationships.create("transfer").from[Source](1).to[Target](1)

    def execute(): Unit = {
      println("Executing from Player.")
      roleQueries.one[Source]().withDraw(amount)
      roleQueries.one[Target]().deposit(amount)
      val from = transferRel.left().head
      val to   = transferRel.right().head
      println(s"Transferred '$amount' from '$from' to '$to'.")
    }

    class Source() {

      def withDraw(m: Double): Unit = {
        val _ = (+this).decrease(m)
      }

    }

    class Target() {

      def deposit(m: Double): Unit = {
        val _ = (+this).increase(m)
      }

    }

  }

}
