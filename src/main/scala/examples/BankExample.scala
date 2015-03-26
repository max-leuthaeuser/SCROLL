package examples

// removes warnings by Eclipse about using structural types

import currency.Currency

import scala.language.reflectiveCalls
import annotations.Role
import internal.{Compartment, DispatchQuery}
import internal.DispatchQuery._
import internal.util.Log.info

object BankExample extends App {

  // Naturals
  case class Person(name: String)

  case class Company(name: String)

  /**
   * Those could also be roles. But here they are only used
   * as Interfaces and bound statically so this would not add any value.
   */
  trait Accountable

  trait Decreasable extends Accountable {
    def decrease(amount: Currency)
  }

  trait Increasable extends Accountable {
    def increase(amount: Currency)
  }

  class Account(var balance: Currency = Currency(0, "USD"))
    extends Increasable
    with Decreasable {

    def increase(amount: Currency) {
      balance = balance + amount
    }

    def decrease(amount: Currency) {
      balance = balance - amount
    }
  }

  // Contexts and Roles
  class Bank extends Compartment {

    @Role class Customer() {
      var accounts = List[Accountable]()

      def addAccount(acc: Accountable) {
        accounts = accounts :+ acc
      }

      def listBalances() {
        accounts.foreach { a => info("Account: " + a + " -> " + (+a).balance) }
      }
    }

    @Role class CheckingsAccount() extends Decreasable {
      def decrease(amount: Currency) {
        dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[CheckingsAccount]).
          Through(_ => true).
          // so we won't calling decrease() recursively on this
          Bypassing(_.isInstanceOf[CheckingsAccount])
        +this decrease amount
      }
    }

    @Role class SavingsAccount() extends Increasable {
      private def transactionFee(amount: Currency) = amount * 0.1

      def increase(amount: Currency) {
        info("Increasing with fee.")
        dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[SavingsAccount]).
          Through(_ => true).
          // so we won't calling increase() recursively on this
          Bypassing(_.isInstanceOf[SavingsAccount])
        +this increase (amount - transactionFee(amount))
      }
    }

    @Role class TransactionRole() {
      def execute() {
        info("Executing from Role.")
        dd = From(_.isInstanceOf[Transaction]).
          To(_.isInstanceOf[TransactionRole]).
          Through(_ => true).
          // so we won't calling execute() recursively on this
          Bypassing(_.isInstanceOf[TransactionRole])
        +this execute()
      }
    }

  }

  class Transaction(val amount: Currency) extends Compartment {
    def execute() {
      info("Executing from Player.")
      // one queries for the first role of the provided type it can find in scope.
      one[Source]().withDraw(amount)
      one[Target]().deposit(amount)
    }

    @Role class Source() {
      def withDraw(m: Currency) {
        +this decrease m
      }
    }

    @Role class Target() {
      def deposit(m: Currency) {
        +this increase m
      }
    }

  }

  // Instance level
  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = new Account(Currency(10.0, "USD"))
  val accForBrian = new Account(Currency(0, "USD"))

  implicit var dd: DispatchQuery = DispatchQuery.empty

  new Bank {
    stan play new Customer
    brian play new Customer
    accForStan play new CheckingsAccount
    accForBrian play new SavingsAccount

    +stan addAccount accForStan
    +brian addAccount accForBrian

    info("### Before transaction ###")
    info("Balance for Stan: " + accForStan.balance)
    info("Balance for Brian: " + accForBrian.balance)

    val transaction = new Transaction(Currency(10.0, "USD"))
    accForStan play new transaction.Source
    accForBrian play new transaction.Target

    // Defining a partOf relation between Transaction and Bank.
    // The transaction needs full access to registered/bound Accounts like
    // CheckingsAccount and SavingsAccount.
    transaction partOf this

    transaction play new TransactionRole execute()

    info("### After transaction ###")
    info("Balance for Stan: " + accForStan.balance)
    info("Balance for Brian: " + accForBrian.balance)
    info("Brian is playing the Customer role? " + (+brian).isPlaying[Customer])

    +stan listBalances()
    +brian listBalances()
  }
}
