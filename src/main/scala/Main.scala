import compartment.Compartment

// basic example
object Main extends App
{

  class University extends Compartment
  {

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

  // instantiate:

  new University
  {
    val hans = Person("hans")
    val uwe = Person("uwe")

    hans.talk()

    val student = new Student
    hans play student
    ~hans talk()

    println(!student name())

    uwe play new Professor
    ~uwe talk()

    ~uwe teach ~hans
  }
}
