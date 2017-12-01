package scroll.examples

import scroll.internal.Compartment
import scroll.internal.util.Log.info

object UniversityExample extends App {

  class University extends Compartment {

    class Student {
      def talk(): Unit = {
        info("I am a student")
      }
    }

    class Professor {
      def teach(student: Person): Unit = student match {
        case s if (+s).isPlaying[Student] =>
          val studentName: String = (+student).name
          info("Teaching: " + studentName)
        case _ => info("Nope! I am only teaching students!")
      }

      def talk(): Unit = {
        info("I am a professor")
      }
    }

  }

  class Person(val name: String) {
    def talk(): Unit = {
      info("I am a person")
    }
  }

  // instantiate:
  new University {
    val hans = new Person("hans")
    val uwe = new Person("uwe")

    hans.talk()

    val student = new Student
    info("Player equals core: " + ((hans play student) == hans))
    +hans talk()

    info((+student).name)
    info("Role core equals core: " + (+student == hans))

    uwe play new Professor
    +uwe talk()
    info("Core equals core playing a role: " + (+uwe == uwe))

    +uwe teach hans
  }
}
