package util

import reflect.runtime.universe._
import reflect.runtime.currentMirror

object TreeString
{

  implicit class SextAnyTreeString[A](a: A)
  {

    private def indent(s: String)
    = s.lines.toStream match {
      case h +: t =>
        (("- " + h) +: t.map {
          "| " + _
        }) mkString "\n"
      case _ => "- "
    }

    /**
     * @return A readable string representation of this value
     */
    def treeString
    : String
    = a match {
      case x: Traversable[_] =>
        x.stringPrefix + ":\n" +
          x.view
            .map {
            _.treeString
          }
            .map {
            indent
          }
            .mkString("\n")
      case x: Product if x.productArity == 0 =>
        x.productPrefix
      case x: Product =>
        x.productPrefix + ":\n" +
          x.productIterator
            .map {
            _.treeString
          }
            .map {
            indent
          }
            .mkString("\n")
      case null =>
        "null"
      case _ =>
        a.toString
    }

    /**
     * @return A readable string representation of this value of a different format to `treeString`
     */
    def valueTreeString
    : String
    = a match {
      case (k, v) =>
        k.valueTreeString + ":\n" +
          v.valueTreeString
      case a: TraversableOnce[_] =>
        a.toStream
          .map(_.valueTreeString)
          .map(indent)
          .mkString("\n")
      case a: Product =>
        val b
        = currentMirror.reflect(a).symbol.typeSignature.members.toStream
          .collect { case a: TermSymbol => a}
          .filterNot(_.isMethod)
          .filterNot(_.isModule)
          .filterNot(_.isClass)
          .map(currentMirror.reflect(a).reflectField)
          .map(f => f.symbol.name.toString.trim -> f.get)
          .reverse
        collection.immutable.ListMap(b: _*).valueTreeString
      case null =>
        "null"
      case _ =>
        a.toString
    }
  }

}