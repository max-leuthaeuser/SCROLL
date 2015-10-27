package scroll.examples

import scroll.internal.annotations.Role
import scroll.internal.Compartment
import scroll.internal.util.Log.info

object UniversityExample extends App {

  class University extends Compartment {

    @Role class Student {
      def talk() {
        info("I am a student")
      }
    }

    @Role class Professor {
      def teach(student: Student) {
        info("Teaching: " + (+student name))
      }

      def talk() {
        info("I am a professor")
      }
    }

  }

  class Person(val name: String) {
    def talk() {
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
    +hans talk ()

    info((+student).name)
    info("Role core equals core: " + (+student == hans))

    uwe play new Professor
    +uwe talk ()
    info("Core equals core playing a role: " + (+uwe == uwe))

    +uwe teach +hans
  }
}
