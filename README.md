SCROLL
======
*SCala ROLes Language*

A playground for role dispatch based on Scala.

**1. Current state:** 

You are able to define compartments, roles and play-relationships. Invoking Role-methods is done via the [Dynamic][scala-dynamic] trait.
  
**2. Example:**
```scala
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

    def getBalance(): CurrencyRepr = balance
  }

  // Contexts and Roles
  class Bank extends Context {

    @Role class Customer() {
      var accounts = List[Accountable]()

      def addAccount(acc: Accountable) {
        accounts = accounts :+ acc
      }

      def listBalances() {
        accounts.foreach { a => info("Account: " + a + " -> " + (+a).getBalance()) }
      }
    }

    @Role class CheckingsAccount() extends Decreasable[CurrencyRepr] {
      def decrease(amount: CurrencyRepr) {
        (-this).decrease(amount)
      }
    }

    @Role class SavingsAccount() extends Increasable[CurrencyRepr] {
      private def transactionFee(amount: CurrencyRepr) = amount * 0.1

      def increase(amount: CurrencyRepr) {
        info("Increasing with fee.")
        (-this).increase(amount - transactionFee(amount))
      }
    }

    @Role class TransactionRole() {
      def execute() {
        info("Executing from Role.")
        (-this).execute()
      }
    }
  }

  class Transaction(val amount: CurrencyRepr) extends Context {
    def execute() {
      // one queries for exactly 1 role of the provided type it can find in scope.
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
    Bind {
      stan With new Customer()
      brian With new Customer()
      accForStan With new CheckingsAccount()
      accForBrian With new SavingsAccount()
    } Blocking {
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
      transaction union this

      // defining the specific dispatch as example
      dd = From(_.is[Transaction]).
        To(_.is[TransactionRole]).
        Through(_ => true).
        Bypassing(_ => true)

      (transaction play new TransactionRole).execute()

      info("### After transaction ###")
      info("Balance for Stan: " + accForStan.balance)
      info("Balance for Brian: " + accForBrian.balance)
      info("Brian is playing the Customer role? " + (+brian).isPlaying[Customer])

      (+stan).listBalances()
      (+brian).listBalances()
      info("### Finished. ###")
    }
  }
}
```

  You can find more examples in the ```examples/``` folder.
  You also might want to check the ```test/```folder.

**3. Edit and run:**

3.1. Clone this repo.

3.2. You may want to use SBT and run ```gen-idea```if you are using Intellij IDE <= 13 (to config see [here][sbt-gen-idea]). This is not required anymore since Intellij 14. Just use the built-in import SBT project functionality.

3.3. You may want to use SBT and run ```eclipse``` if you are using the Eclipse Scala IDE. (to config see [here][gen-eclipse])

[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
