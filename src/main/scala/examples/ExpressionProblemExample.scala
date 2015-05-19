package examples

import internal.Compartment
import internal.util.Log.info

object ExpressionProblemExample extends App {

  // M1
  trait M1 extends Compartment {

    trait Exp {
      def eval(): Int
    }

    case class Num(value: Int) extends Exp {
      override def eval(): Int = value
    }

    case class Add(left: Exp, right: Exp) extends Exp {
      override def eval(): Int = left.eval() + right.eval()
    }

  }

  // M2
  trait M2 extends M1 {

    case class Neg(exp: Exp) extends Exp {
      override def eval(): Int = -exp.eval
    }

  }

  // M3
  trait M3 extends M2 {

    class NumShowable() {
      def show(): String = {
        val value: Int = (+this).value
        value.toString
      }
    }

    class AddShowable() {
      def show(): String = {
        val left: Exp = (+this).left
        val right: Exp = (+this).right
        s"${(+left).show()} + ${(+right).show()}"
      }
    }

  }

  // M4
  trait M4 extends M3 {

    class NegShowable() {
      def show(): String = {
        val e: Exp = (+this).exp
        s"-${(+e).show()}"
      }
    }

  }

  new M4 {
    val n1 = Num(11)
    n1 play new NumShowable()
    val n2 = Num(2)
    n2 play new NumShowable()
    val neg = Neg(n2)
    neg play new NegShowable()
    val e = Add(neg, n1) play new AddShowable()

    info("Eval: " + e.eval())
    info("Show: " + e.show())
  }

}