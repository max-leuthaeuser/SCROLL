package examples

import java.util
import internal.Compartment
import internal.DispatchQuery._
import internal.util.Log.info

object LegacyCodeExample extends App {

  class LegacyCode extends Compartment {

    case class BrokenOldList[T]() {
      def add(item: T) {
        info(s"Nope, this is broken. Item '$item' was not added!")
      }
    }

    case class Iterateable[T]() {
      def foreach(f: T => Unit) {
        (0 until (+this).size).foreach(i => f((+this).get(i)))
      }

      def addAll(list: util.LinkedList[T]) {
        implicit val dd = From(anything).
          To(_.isInstanceOf[Iterateable[T]]).
          Through(anything).
          Bypassing(_.isInstanceOf[Iterateable[T]])
        +this addAll list
      }
    }

  }

  new LegacyCode {
    val newFancyList = new util.LinkedList[String]()
    val oldList = BrokenOldList[String]()

    val fixedList = oldList play newFancyList

    // wont work
    oldList add "item0"

    // but now
    fixedList add "item1"
    fixedList add "item2"
    fixedList add "item3"

    // and now we patch foreach to it
    fixedList play Iterateable()

    // double the fun
    fixedList addAll fixedList

    fixedList.foreach((a: String) => info(a))
  }
}
