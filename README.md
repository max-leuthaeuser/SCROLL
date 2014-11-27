RoleDispatch
============

Role playground for role dispatch based on Scala.


1. Current state
  
  You are able to define compartments, roles and play-relationships. Invoking
  Role-methods is done via the [Dynamic][scala-dynamic] trait.
  
2. Example
  ```scala
  object BankExample extends App
  {
  
    // Naturals
    case class Person(name: String)
  
    case class Company(name: String)
  
    class Account(var balance: Double = 0)
    {
      def increase(amount: Double)
      {
        balance = balance + amount
      }
  
      def decrease(amount: Double)
      {
        balance = balance - amount
      }
    }
  
    // Contexts and Roles
    class Bank extends Context
    {
  
      @Role case class Customer()
      {
        var accounts = List[Either[CheckingsAccount, SavingsAccount]]()
  
        def addAccount(acc: Either[CheckingsAccount, SavingsAccount]) =
        {
          accounts = accounts :+ acc
        }
      }
  
      @Role class CheckingsAccount()
      {
        def decrease(amount: Double)
        {
          (-this).decrease(amount)
        }
      }
  
      @Role class SavingsAccount()
      {
        private def transactionFee(amount: Double) = amount * 0.1
  
        def decrease(amount: Double)
        {
          (-this).decrease(amount - transactionFee(amount))
        }
      }
  
      @Role class TransactionRole()
      {
        def execute()
        {
          (-this).execute()
        }
      }
  
    }
  
    class Transaction(val amount: Double) extends Context
    {
      def execute()
      {
        E_?(Source).withDraw(amount)
        E_?(Target).deposit(amount)
      }
  
      // to make roles that are contained in some Compartment accessible one
      // has to create some helper methods like the following
      def Source = new Source
  
      def Target = new Target
  
      @Role class Source()
      {
        def withDraw(m: Double)
        {
          (+this).decrease(m)
        }
      }
  
      @Role class Target()
      {
        def deposit(m: Double)
        {
          (+this).increase(m)
        }
      }
  
    }
  
    // Instance level
    val stan = Person("Stan")
    val brian = Person("Brian")
  
    val accForStan = new Account(10.0)
    val accForBrian = new Account(0)
  
    new Bank
    {
      Bind(stan With Customer(),
        brian With Customer(),
        accForStan With new CheckingsAccount(),
        accForBrian With new CheckingsAccount()) {
  
        (+stan).addAccount(Left(accForStan))
        (+brian).addAccount(Left(accForBrian))
  
        println("### Before transaction ###")
        println("Balance for Stan: " + accForStan.balance)
        println("Balance for Brian: " + accForBrian.balance)
  
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
  
        println("### After transaction ###")
        println("Balance for Stan: " + accForStan.balance)
        println("Balance for Brian: " + accForBrian.balance)
  
        println((+brian).isPlaying[Customer])
        println(t.isPlaying[TransactionRole])
      }
    }
  }
  ```

  You can find more examples in the ```examples/``` folder.
  You also might want to check the ```test/```folder.

3. Edit and run

  3.1. Clone this repo.
  
  3.2. You may want to use SBT and run ```gen-idea```if you are using Intellij IDE. (to config see [here][sbt-gen-idea])

  3.3. You may want to use SBT and run ```eclipse``` if you are using the Eclipse Scala IDE. (to config see [here][gen-eclipse])

[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[gen-eclipse]: https://github.com/typesafehub/sbteclipse
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
