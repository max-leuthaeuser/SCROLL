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
      def talk()
      {
        println("I am a professor")
      }
    }

  }

  class Person
  {
    def talk()
    {
      println("I am a person")
    }
  }

  // instantiate:

  new University
  {
    val hans = new Person

    hans.talk()
    val student = new Student
    hans play student
    ~hans talk()

    hans drop student

    hans play new Professor
    ~hans talk()
  }
}
