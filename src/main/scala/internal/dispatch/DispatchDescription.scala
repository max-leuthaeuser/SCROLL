package internal.dispatch

import scala.collection.mutable

object DispatchDescription
{
  def When(when: () => Boolean) = new
    {
      def Dispatch(rules: DispatchRule*) = DispatchDescription(when, rules.toList)
    }
}

case class DispatchDescription(
  when: () => Boolean,
  rules: List[DispatchRule]
  )
{

  type Plays = (String, String)
  type Score = (String, Int)

  val scores = mutable.Map[Plays, List[Score]]()

  calculateScores()

  def calculateScores()
  {
    rules.foreach(r => {
      val key = (r.in, r.role)
      val value = mutable.ArrayBuffer[Score]()
      r.precs.foreach {
        case Before(left, right) => {
          val cur = value.find(_._1.equals(left)).getOrElse((left, 0))
          value.append((left, cur._2 + 1))
        }
        case After(left, right) => {
          val cur = value.find(_._1.equals(right)).getOrElse((right, 0))
          value.append((right, cur._2 + 1))
        }
        case Replace(left, right) => (right, 0)
      }
      scores(key) = value.toList
    })
  }
}

object DispatchRule
{
  def In(i: String) = new
    {
      def With(w: String)
        (st: Statement*) = DispatchRule(i, w, st.toList)
    }
}

case class DispatchRule(
  in: String,
  role: String,
  precs: List[Statement]
  )

object Statement
{
  def invoke(s: => String): Statement =
  {
    if (s.contains(Before.repr)) {
      val (l, r) = s.splitAt(s.indexOf(Before.repr))
      return Before(l, r.replace(Before.repr, ""))
    }
    if (s.contains(Replace.repr)) {
      val (l, r) = s.splitAt(s.indexOf(Replace.repr))
      return Replace(l, r.replace(Replace.repr, ""))
    }
    if (s.contains(After.repr)) {
      val (l, r) = s.splitAt(s.indexOf(After.repr))
      return After(l, r.replace(After.repr, ""))
    }
    throw new IllegalArgumentException("'" + s + "' is no valid Statement.")
  }
}

abstract class Statement(
  left: String,
  right: String
  )
{
}

object Before
{
  def repr = " before "
}

case class Before(
  left: String,
  right: String
  ) extends Statement(left, right)

object Replace
{
  def repr = " replace "
}

case class Replace(
  left: String,
  right: String
  ) extends Statement(left, right)

object After
{
  def repr = " after "
}

case class After(
  left: String,
  right: String
  ) extends Statement(left, right)