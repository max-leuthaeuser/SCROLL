package scroll.examples

import scroll.internal.annotations.Role
import scroll.examples.currency.Currency
import scroll.internal.support.DispatchQuery
import DispatchQuery._
import scroll.internal.util.Log.info
import scroll.internal.Compartment

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
    def decrease(amount: Currency): Unit
  }

  trait Increasable extends Accountable {
    def increase(amount: Currency): Unit
  }

  class Account(var balance: Currency = Currency(0, "USD"))
    extends Increasable
      with Decreasable {

    def increase(amount: Currency): Unit = {
      balance = balance + amount
    }

    def decrease(amount: Currency): Unit = {
      balance = balance - amount
    }
  }

  // Contexts and Roles
  class Bank extends Compartment {

    @Role class Customer() {
      var accounts = List[Accountable]()

      def addAccount(acc: Accountable): Unit = {
        accounts = accounts :+ acc
      }

      def listBalances(): Unit = {
        accounts.foreach { a => info("Account: " + a + " -> " + (+a).balance) }
      }
    }

    @Role class CheckingsAccount() extends Decreasable {
      def decrease(amount: Currency): Unit = {
        dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[CheckingsAccount]).
          Through(anything).
          // so we won't calling decrease() recursively on this
          Bypassing(_.isInstanceOf[CheckingsAccount])
        val _ = +this decrease amount
      }
    }

    @Role class SavingsAccount() extends Increasable {
      private def transactionFee(amount: Currency): Currency = amount * 0.1

      def increase(amount: Currency): Unit = {
        info("Increasing with fee.")
        dd = From(_.isInstanceOf[Account]).
          To(_.isInstanceOf[SavingsAccount]).
          Through(anything).
          // so we won't calling increase() recursively on this
          Bypassing(_.isInstanceOf[SavingsAccount])
        val _ = +this increase (amount - transactionFee(amount))
      }
    }

    @Role class TransactionRole() {
      def execute(): Unit = {
        info("Executing from Role.")
        dd = From(_.isInstanceOf[Transaction]).
          To(_.isInstanceOf[TransactionRole]).
          Through(anything).
          // so we won't calling execute() recursively on this
          Bypassing(_.isInstanceOf[TransactionRole])
        val _ = +this execute()
      }
    }

  }

  class Transaction(val amount: Currency) extends Compartment {
    def execute(): Unit = {
      info("Executing from Player.")
      // one queries for the first role of the provided type it can find in scope.
      one[Source]().withDraw(amount)
      one[Target]().deposit(amount)
    }

    @Role class Source() {
      def withDraw(m: Currency): Unit = {
        val _ = +this decrease m
      }
    }

    @Role class Target() {
      def deposit(m: Currency): Unit = {
        val _ = +this increase m
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
