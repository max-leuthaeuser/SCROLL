package examples

import internal.Context
import annotations.Role

object BankExample extends App {

  // Naturals
  case class Person(name: String)

  case class Company(name: String)

  class Account(var balance: Double = 0) {
    def increase(amount: Double) {
      balance = balance + amount
    }

    def decrease(amount: Double) {
      balance = balance - amount
    }
  }

  // Contexts and Roles
  class Bank extends Context {
    @Role case class Customer() {
      var accounts = List[Either[CheckingsAccount, SavingsAccount]]()

      def addAccount(acc: Either[CheckingsAccount, SavingsAccount]) = {
        accounts = accounts :+ acc
      }
    }

    @Role class CheckingsAccount() {
      def decrease(amount: Double) {
        (!this).decrease(amount)
      }
    }

    @Role class SavingsAccount() {
      private def transactionFee(amount: Double) = amount * 0.1

      def decrease(amount: Double) {
        (!this).decrease(amount - transactionFee(amount))
      }
    }

    @Role class TransactionRole() {
      def execute() {
        (!this).execute()
      }
    }

  }

  class Transaction(val amount: Double) extends Context {
    def execute() {
      E_?(Source()).withDraw(amount)
      E_?(Target()).deposit(amount)
    }

    // to make roles that are contained in some Compartment accessible one
    // has to create some helper methods like the following
    def Source() = new Source

    def Target() = new Target

    @Role class Source() {
      def withDraw(m: Double) {
        (~this).decrease(m)
      }
    }

    @Role class Target() {
      def deposit(m: Double) {
        (~this).increase(m)
      }
    }

  }

  // Instance level
  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = new Account()
  val accForBrian = new Account()

  new Bank {
    Bind(stan With Customer(),
      brian With Customer(),
      accForStan With new CheckingsAccount(),
      accForBrian With new CheckingsAccount()) {

      (~stan).addAccount(Left(accForStan))
      (~brian).addAccount(Left(accForBrian))

      println("### Before transaction ###")
      println("Balance for Stan: " + accForStan.balance)
      println("Balance for Brian: " + accForBrian.balance)

      val transaction = new Transaction(10.0)

      accForStan play transaction.Source()
      accForBrian play transaction.Target()

      // transaction is currently a part of the Bank context
      transaction >:> this

      (transaction play new TransactionRole).execute()

      println("\n### After transaction ###")
      println("Balance for Stan: " + accForStan.balance)
      println("Balance for Brian: " + accForBrian.balance)
    }
  }

}
