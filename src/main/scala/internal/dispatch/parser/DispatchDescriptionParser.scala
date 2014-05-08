package internal.dispatch.parser

import internal.dispatch.model.{Condition, DispatchDescription}
import scala.util.parsing.combinator.JavaTokenParsers

class DispatchDescriptionParser extends JavaTokenParsers
{
  // ignore whitespaces and all c-style comments
  protected override val whiteSpace = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  def dispatchDescription: Parser[DispatchDescription] = conditions ^^ {
    case l => DispatchDescription(l)
  }

  def conditions: Parser[List[Condition]] = rep(condition)

  def condition: Parser[Condition] = ???

  def parse(p: String): DispatchDescription =
    parseAll(dispatchDescription, p) match {
      case Success(r, _) => r
      case e => throw new Exception("Invalid DispatchDescription source:\n" + e.toString)
    }
}