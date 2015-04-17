package examples

import java.util
import internal.Compartment
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

    fixedList.foreach((a: String) => info(a))
  }
}
