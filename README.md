RoleDispatch
============

Role playground for role dispatch based on Scala.


1. Current state
  
  You are able to define compartments, roles and play-relationships. Invoking
  Role-methods is done via the [Dynamic][scala-dynamic] trait. There is no way to define a
  custom invokation dispatch algorithm yet.
  
2. Example
  ```scala
  object Main extends App
  {
  
    class University extends Compartment
    {
      // defining some simple roles
      class Student
      {
        def talk()
        {
          println("I am a student")
        }
      }
    
      class Professor
      {
        def teach(student: Student)
        {
          // the role core is accessed via '!'
          println("Teaching: " + (!student name()))
        }
    
        def talk()
        {
          println("I am a professor")
        }
      }
    
    }
    
    case class Person(name: String)
    {
      def talk()
      {
        println("I am a person")
      }
    }
    
    // instantiate a new compartment
    new University
    {
      // instantiating 2 player objects
      val hans = Person("hans")
      val uwe = Person("uwe")
    
      hans.talk()
    
      // instantiating some role
      val student = new Student
      // and binding it using 'play'
      println("Player equals core: " + ((hans play student) == hans))
      // invoking role methods via '~'
      ~hans talk()
    
      println(!student name())
      println("Role core equals core: " + (!student == hans))
    
      uwe play new Professor
      ~uwe talk()
      println("Core equals core playing a role: " + (~uwe == uwe))
    
      // providing the role of the object 'hans'
      // is currently playing as argument
      ~uwe teach ~hans
    }
    
  }
  ```

3. Edit and run

  3.1. Clone this repo.
  
  3.2. You may want to use SBT and run ```gen-idea``` (to config see [here][sbt-gen-idea])
  
[sbt-gen-idea]: https://github.com/mpeltonen/sbt-idea
[scala-dynamic]: http://www.scala-lang.org/api/current/#scala.Dynamic
