package scroll.internal.formal

object FormalUtils {

  def mutualDisjoint[T <: Any](sets: List[List[T]]): Boolean = {
    val all = sets.map(_.distinct).flatten
    all.size == all.distinct.size
  }

  def totalFunction[T, RT >: Null](domain: List[T], foo: Map[T, List[RT]]): Boolean = domain.toSet.subsetOf(foo.keySet)

  def all(on: List[Boolean]): Boolean = !on.contains(false)

  def any(on: List[Boolean]): Boolean = on.contains(true)

  def atoms[T >: Null](a: Any): List[T] = a match {
    // TODO: fix asInstanceOf
    case elem: String => List(elem).asInstanceOf[List[T]]
    case elem: FormalRoleGroup => elem.rolegroups.map(atoms).flatten.distinct
  }

  def evaluate[NT >: Null, RT >: Null, CT >: Null, RST >: Null](a: Any, croi: FormalCROI[NT, RT, CT, RST], o: NT, c: CT): Int = a match {
    case elem: String => any(croi.r.filter(croi.type1(_) == a).map(rr => croi.plays.contains((o, c, rr)))) match {
      case true => 1
      case false => 0
    }
    case elem: FormalRoleGroup =>
      val sum = elem.rolegroups.map(evaluate(_, croi, o, c)).sum
      if (elem.lower <= sum && sum <= elem.upper) 1 else 0
  }
}
