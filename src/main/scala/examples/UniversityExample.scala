package examples

import internal.Compartment
import annotations.Role

object UniversityExample extends App {

  class University extends Compartment {

    @Role class Student {
      def talk() {
        println("I am a student")
      }
    }

    @Role class Professor {
      def teach(student: Student) {
        println("Teaching: " + (-student name ()))
      }

      def talk() {
        println("I am a professor")
      }
    }

  }

  case class Person(name: String) {
    def talk() {
      println("I am a person")
    }
  }

  // instantiate:
  new University {
    val hans = Person("hans")
    val uwe = Person("uwe")

    hans.talk()

    val student = new Student
    println("Player equals core: " + ((hans play student) == hans))
    +hans talk ()

    println(-student name ())
    println("Role core equals core: " + (-student == hans))

    uwe play new Professor
    +uwe talk ()
    println("Core equals core playing a role: " + (+uwe == uwe))

    +uwe teach +hans
  }
}
