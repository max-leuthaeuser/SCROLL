package examples

import internal.Compartment
import util.Log.info
import annotations.Role

object UniversityExample extends App {

  class University extends Compartment {

    @Role class Student {
      def talk() {
        info("I am a student")
      }
    }

    @Role class Professor {
      def teach(student: Student) {
        info("Teaching: " + (-student name ()))
      }

      def talk() {
        info("I am a professor")
      }
    }

  }

  case class Person(name: String) {
    def talk() {
      info("I am a person")
    }
  }

  // instantiate:
  new University {
    val hans = Person("hans")
    val uwe = Person("uwe")

    hans.talk()

    val student = new Student
    info("Player equals core: " + ((hans play student) == hans))
    +hans talk ()

    info(-student name ())
    info("Role core equals core: " + (-student == hans))

    uwe play new Professor
    +uwe talk ()
    info("Core equals core playing a role: " + (+uwe == uwe))

    +uwe teach +hans
  }
}
