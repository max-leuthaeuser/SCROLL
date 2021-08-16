package scroll.examples

import scroll.internal.compartment.impl.Compartment

object UniversityExample {

  @main def runUniversityExample(): Unit =
    new University {
      val hans = new Person("hans")
      val uwe  = new Person("uwe")

      hans.talk()

      val student = new Student
      println("Player equals core: " + ((hans play student) == hans))
      (+hans).talk()

      val name: String = (+student).name
      println(name)
      println("Role core equals core: " + (+student == hans))

      uwe play new Professor
      (+uwe).talk()
      println("Core equals core playing a role: " + (+uwe == uwe))

      (+uwe).teach(hans)
    }

  class University extends Compartment {

    class Student {
      def talk(): Unit = println("I am a student")
    }

    class Professor {

      def teach(student: Person): Unit =
        student match {
          case s if (+s).isPlaying[Student] =>
            val studentName: String = (+student).name
            println("Teaching: " + studentName)
          case _ => println("Nope! I am only teaching students!")
        }

      def talk(): Unit = println("I am a professor")
    }

  }

  class Person(val name: String) {
    def talk(): Unit = println("I am a person")
  }

}
