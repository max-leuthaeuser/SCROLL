package scroll.internal.formal

/** Collection of some helper functions for the formal representation of CROM.
  */
object FormalUtils {

  /** @param sets
    *   the list of sets to check
    * @tparam T
    *   the type of the contained elements
    * @return
    *   true iff the given sets are mutually disjoint to each other
    */
  def mutualDisjoint[T](sets: List[List[T]]): Boolean = {
    val all = sets.flatMap(_.distinct)
    all.lengthCompare(all.distinct.size) == 0
  }

  /** @return
    *   true iff the mapping in foo provides a total function in the domain of 'domain'
    */
  def totalFunction[T, RT](domain: List[T], foo: Map[T, List[RT]]): Boolean =
    domain.toSet.subsetOf(foo.keySet)

  /** @return
    *   true iff the provided list only contains true, false otherwise
    */
  def all(on: List[Boolean]): Boolean = !on.contains(false)

  /** @return
    *   true iff the provided list contains true at least once, false otherwise.
    */
  def any(on: List[Boolean]): Boolean = on.contains(true)

  def atoms[T >: Null <: AnyRef](a: AnyRef): List[T] =
    a match {
      case elem: String          => List(elem).asInstanceOf[List[T]]
      case elem: FormalRoleGroup => elem.rolegroups.flatMap(atoms).distinct
      case _                     => throw new IllegalArgumentException(s"Can't handle: '$a'")
    }

  def evaluate[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef](
    a: AnyRef,
    croi: FormalCROI[NT, RT, CT, RST],
    o: NT,
    c: CT
  ): Int =
    a match {
      case _: String if any(croi.r.filter(croi.type1(_) == a).map(rr => croi.plays.contains((o, c, rr)))) =>
        1
      case _: String => 0
      case elem: FormalRoleGroup =>
        val sum = elem.rolegroups.map(evaluate(_, croi, o, c)).sum
        if (elem.lower <= sum && sum <= elem.upper) 1 else 0
      case _ => throw new IllegalArgumentException(s"Can't handle: '$a'")
    }

}
