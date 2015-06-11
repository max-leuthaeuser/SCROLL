package examples

import internal.Compartment
import org.kiama.attribution.Attribution.attr
import org.kiama.rewriting.Rewriter.{attempt, bottomup, rewrite, rule}
import org.kiama.util.{ParserUtilities, TreeNode}

object MathKiamaExample extends App with Compartment {

  sealed abstract class Exp extends TreeNode

  case class Num(i: Int) extends Exp

  case class Add(l: Exp, r: Exp) extends Exp

  case class Mul(l: Exp, r: Exp) extends Exp

  case class SimpleMath() {
    val value: Exp => Int =
      attr {
        case Num(i) => i
        case Add(l, r) => (l -> value) + (r -> value)
        case Mul(l, r) => (l -> value) * (r -> value)
      }
  }

  case class Optimizer() {

    def optimise(e: Exp): Exp = rewrite(optimiser)(e)

    lazy val optimiser = bottomup(attempt(simplifier))

    lazy val simplifier =
      rule[Exp] {
        case Add(Num(0), e) => e
        case Add(e, Num(0)) => e
        case Mul(Num(1), e) => e
        case Mul(e, Num(1)) => e
        case Mul(z@Num(0), _) => z
        case Mul(_, z@Num(0)) => z
      }
  }

  case class Parser() extends ParserUtilities {

    def parse(in: String): Exp = parseString(parser, in) match {
      case Left(a) => a
      case Right(err) => throw new IllegalArgumentException(in + " is no valid Exp! " + err)
    }

    lazy val parser = phrase(exp)

    lazy val exp: PackratParser[Exp] = exp ~ ("+" ~> term) ^^ Add | term

    lazy val term: PackratParser[Exp] = term ~ ("*" ~> factor) ^^ Mul | factor

    lazy val factor: PackratParser[Exp] = integer | "(" ~> exp <~ ")"

    lazy val integer = "[0-9]+".r ^^ (s => Num(s.toInt))

  }

  // make it run
  val someMath = SimpleMath() play Optimizer() play Parser()

  val ast: Exp = someMath.parse("1+2*3*0")
  val optimizedAst: Exp = someMath.optimise(ast)
  val resultFunc: Exp => Int = someMath.value
  val result = resultFunc(optimizedAst)

  println("AST: " + ast)
  println("optimized AST: " + optimizedAst)
  println("Result: " + result)
  assert(1 == result)
}
