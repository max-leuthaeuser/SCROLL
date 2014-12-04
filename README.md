RoleDispatch
============
Role playground for role dispatch based on Scala.

**1. Current state:** 

You are able to define compartments, roles and play-relationships. Invoking Role-methods is done via the [Dynamic][scala-dynamic] trait.
  
**2. Example:**
  ```scala
  object BankExample extends App {
      // Naturals
      case class Person(name: String)
      case class Company(name: String)
    
      /**
       * Those both could also be roles. But here they are only used
       * as Interfaces and bound statically so this would not add value.
       */
      trait Decreasable[T] {
        def decrease(amount: T)
      }
      trait Increasable[T] {
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
          var accounts = List[Decreasable[CurrencyRepr]]()
    
          def addAccount(acc: Decreasable[CurrencyRepr]) {
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
    
        @Role class SavingsAccount() extends Decreasable[CurrencyRepr] {
          private def transactionFee(amount: CurrencyRepr) = amount * 0.1
    
          def decrease(amount: CurrencyRepr) {
            (-this).decrease(amount - transactionFee(amount))
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
          info("Executing from Player.")
          // E_? selects the first entity playing the given role it can find
          E_?(Source).withDraw(amount)
          E_?(Target).deposit(amount)
        }
    
        // to make roles that are contained in some Compartment accessible one
        // has to create some helper methods like the following:
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
    
      new Bank {
        Bind {
          stan With new Customer()
          brian With new Customer()
          accForStan With new CheckingsAccount()
          accForBrian With new CheckingsAccount()
        } Blocking {
          (+stan).addAccount(accForStan)
          (+brian).addAccount(accForBrian)
    
          info("### Before transaction ###")
          info("Balance for Stan: " + accForStan.balance)
          info("Balance for Brian: " + accForBrian.balance)
    
          lazy val transaction = new Transaction(10.0)
    
          accForStan play transaction.Source
          accForBrian play transaction.Target
    
          // transaction is currently a part of the Bank context
          transaction >+> this
    
          // defining the specific dispatch
          lazy implicit val dd: DispatchQuery =
            From(_.is[Transaction]).
              To(_.is[TransactionRole]).
              Through(_ => true).
              Bypassing(_ => true)
    
          val t = transaction play new TransactionRole
          t.execute()
    
          info("### After transaction ###")
          info("Balance for Stan: " + accForStan.balance)
          info("Balance for Brian: " + accForBrian.balance)
          info("Brian is playing the Customer role? " + (+brian).isPlaying[Customer])
          info("The transaction is playing the TransactionRole? " + t.isPlaying[TransactionRole])
    
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

3.2. You may want to use SBT and run ```gen-idea```if you are using Intellij IDE. (to config see [here][sbt-gen-idea])

3.3. You may want to use SBT and run ```eclipse``` if you are using the Eclipse Scala IDE. (to config see [here][gen-eclipse])

[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
