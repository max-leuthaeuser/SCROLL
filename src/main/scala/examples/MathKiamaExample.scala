package examples

import internal.Compartment
import org.kiama.attribution.Attribution.attr
import org.kiama.rewriting.Rewriter.{attempt, bottomup, rewrite, rule}
import org.kiama.util.TreeNode

import scala.util.parsing.combinator.JavaTokenParsers

object MathKiamaExample extends App with Compartment {

  sealed abstract class Exp extends TreeNode

  case class Num(i: Double) extends Exp

  case class Add(l: Exp, r: Exp) extends Exp

  case class Sub(l: Exp, r: Exp) extends Exp

  case class Mul(l: Exp, r: Exp) extends Exp

  case class Div(l: Exp, r: Exp) extends Exp

  case class SimpleMath() {
    val value: Exp => Double =
      attr {
        case Num(i) => i
        case Add(l, r) => (l -> value) + (r -> value)
        case Sub(l, r) => (l -> value) - (r -> value)
        case Mul(l, r) => (l -> value) * (r -> value)
        case Div(l, r) => (l -> value) / (r -> value)
      }
  }

  case class Optimizer() {

    def optimise(e: Exp): Exp = rewrite(optimiser)(e)

    lazy val optimiser = bottomup(attempt(simplifier))

    lazy val simplifier =
      rule[Exp] {
        case Add(Num(0), e) => e
        case Add(e, Num(0)) => e
        case Sub(Num(0), e) => e
        case Sub(e, Num(0)) => e
        case Mul(Num(1), e) => e
        case Mul(e, Num(1)) => e
        case Mul(z@Num(0), _) => z
        case Mul(_, z@Num(0)) => z
        case Div(e, Num(1)) => e
        case Div(_, Num(0)) => throw new IllegalArgumentException("Division by 0!")
      }
  }

  case class Parser() extends JavaTokenParsers {

    def parse(in: String): Exp = parseAll(expr, in).get

    lazy val expr: Parser[Exp] = term ~ rep("[+-]".r ~ term) ^^ {
      case t ~ ts => ts.foldLeft(t) {
        case (t1, "+" ~ t2) => Add(t1, t2)
        case (t1, "-" ~ t2) => Sub(t1, t2)
      }
    }

    lazy val term = factor ~ rep("[*/]".r ~ factor) ^^ {
      case t ~ ts => ts.foldLeft(t) {
        case (t1, "*" ~ t2) => Mul(t1, t2)
        case (t1, "/" ~ t2) => Div(t1, t2)
      }
    }

    lazy val factor = "(" ~> expr <~ ")" | num

    lazy val num = floatingPointNumber ^^ (s => Num(s.toDouble))

  }

  // make it run
  val someMath = SimpleMath() play Optimizer() play Parser()

  val ast: Exp = someMath.parse("1+2+3*0")
  val optimizedAst: Exp = someMath.optimise(ast)
  val resultFunc: Exp => Double = someMath.value
  val result = resultFunc(optimizedAst)

  println("AST: " + ast)
  println("optimized AST: " + optimizedAst)
  println("Result: " + result)
  assert(3 == result)
}
