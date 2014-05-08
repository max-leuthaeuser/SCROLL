package internal.dispatch.parser

import internal.dispatch.model._
import scala.util.parsing.combinator.JavaTokenParsers

class DispatchDescriptionParser extends JavaTokenParsers
{
  // ignore whitespaces and all c-style comments
  protected override val whiteSpace = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  def dispatchDescription: Parser[DispatchDescription] = conditions ^^ {
    case l => DispatchDescription(l)
  }

  def conditions: Parser[List[Condition]] = rep(condition)

  def condition: Parser[Condition] = "condition" ~ ident ~ ":" ~ rule ~ dispatch ^^ {
    case "condition" ~ name ~ ":" ~ r ~ d => Condition(name, r, d)
  }

  def rule: Parser[Rule] = ???

  def dispatch: Parser[Dispatch] = ???

  def parse(p: String): DispatchDescription =
    parseAll(dispatchDescription, p) match {
      case Success(r, _) => r
      case e => throw new Exception("Invalid DispatchDescription source:\n" + e.toString)
    }
}