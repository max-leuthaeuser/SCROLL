package examples

// removes warnings by Eclipse about using structural types

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

  trait Decreasable[T] extends Accountable {
    def decrease(amount: T)
  }

  trait Increasable[T] extends Accountable {
    def increase(amount: T)
  }

  type CurrencyRepr = Double

  class Account(var balance: CurrencyRepr = 0)
    extends Increasable[CurrencyRepr]
    with Decreasable[CurrencyRepr] {

    def increase(amount: CurrencyRepr) {
      balance = balance + amount
    }

    def decrease(amount: CurrencyRepr) {
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
        accounts.foreach { a => info("Account: " + a + " -> " + (+a).balance)}
      }
    }

    @Role class CheckingsAccount() extends Decreasable[CurrencyRepr] {
      def decrease(amount: CurrencyRepr) {
        (+this).decrease(amount)
      }
    }

    @Role class SavingsAccount() extends Increasable[CurrencyRepr] {
      private def transactionFee(amount: CurrencyRepr) = amount * 0.1

      def increase(amount: CurrencyRepr) {
        info("Increasing with fee.")
        (+this).increase(amount - transactionFee(amount))
      }
    }

    @Role class TransactionRole() {
      def execute() {
        info("Executing from Role.")
        (+this).execute()
      }
    }

  }

  class Transaction(val amount: CurrencyRepr) extends Compartment {
    def execute() {
      info("Executing from Player.")
      // one queries for the first role of the provided type it can find in scope.
      one[Source]().withDraw(amount)
      one[Target]().deposit(amount)
    }

    // To make roles that are contained in some Compartment accessible one
    // has to create some helper methods like the following
    def Source = new Source()

    def Target = new Target()

    @Role class Source() {
      def withDraw(m: CurrencyRepr) {
        (+this).decrease(m)
      }
    }

    @Role class Target() {
      def deposit(m: CurrencyRepr) {
        (+this).increase(m)
      }
    }

  }

  // Instance level
  val stan = Person("Stan")
  val brian = Person("Brian")

  val accForStan = new Account(10.0)
  val accForBrian = new Account(0)

  implicit var dd: DispatchQuery = DispatchQuery.empty

  new Bank {
    stan play new Customer()
    brian play new Customer()
    accForStan play new CheckingsAccount()
    accForBrian play new SavingsAccount()

    (+stan).addAccount(accForStan)
    (+brian).addAccount(accForBrian)

    info("### Before transaction ###")
    info("Balance for Stan: " + accForStan.balance)
    info("Balance for Brian: " + accForBrian.balance)

    val transaction = new Transaction(10.0)
    accForStan play transaction.Source
    accForBrian play transaction.Target

    // Defining a bidirectional relation between Transaction and Bank.
    // The transaction needs full access to registered/bound Accounts like
    // CheckingsAccount and SavingsAccount.
    transaction partOf this

    // defining the specific dispatch as example
    dd = From(_.isInstanceOf[Transaction]).
      To(_.isInstanceOf[TransactionRole]).
      Through(_ => true).
      Bypassing(_ => false)

    (transaction play new TransactionRole).execute()

    info("### After transaction ###")
    info("Balance for Stan: " + accForStan.balance)
    info("Balance for Brian: " + accForBrian.balance)
    info("Brian is playing the Customer role? " + (+brian).isPlaying[Customer])

    (+stan).listBalances()
    (+brian).listBalances()
  }
}
