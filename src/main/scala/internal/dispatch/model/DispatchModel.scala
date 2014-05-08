package internal.dispatch.model

sealed abstract class Elem

case class DispatchDescription(cond: List[Condition]) extends Elem

case class Condition(
  name: String,
  rule: Rule,
  dis: Dispatch
  ) extends Elem

case class Rule(expr: RuleExpression) extends Elem

sealed abstract class Expression extends Elem

sealed abstract class RuleExpression extends Expression

case class When(body: String) extends RuleExpression

case class And(expr: RuleExpression) extends RuleExpression

case class Or(expr: RuleExpression) extends RuleExpression

case class Dispatch(in: List[In]) extends Elem

case class In(
  clazz: String,
  role: String,
  clauses: List[Clause]
  ) extends Elem

sealed abstract class Clause(
  left: Literal,
  right: Literal
  ) extends Elem

case class Before(
  left: Literal,
  right: Literal
  ) extends Clause(left, right)

case class Replace(
  left: Literal,
  right: Literal
  ) extends Clause(left, right)

case class After(
  left: Literal,
  right: Literal
  ) extends Clause(left, right)

sealed abstract class Literal extends Elem

case class Method(name: String) extends Literal

case class Always() extends Literal

case class Never() extends Literal
