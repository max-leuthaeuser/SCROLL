package scroll.internal.formal

import FormalUtils._

object CROM {
  def empty[NT >: Null, RT >: Null, CT >: Null, RST >: Null] = CROM[NT, RT, CT, RST](List.empty, List.empty, List.empty, List.empty, List.empty, Map.empty, Map.empty)

  /**
   * Little helper factory method for creating a CROM with Strings only.
   */
  def forStrings(
                  nt: List[String],
                  rt: List[String],
                  ct: List[String],
                  rst: List[String],
                  fills: List[(String, String)],
                  parts: Map[String, List[String]],
                  rel: Map[String, List[String]]
                  ): CROM[String, String, String, String] = CROM(nt, rt, ct, rst, fills, parts, rel)
}

case class CROM[NT >: Null, RT >: Null, CT >: Null, RST >: Null](
                                                                  nt: List[NT],
                                                                  rt: List[RT],
                                                                  ct: List[CT],
                                                                  rst: List[RST],
                                                                  fills: List[(NT, RT)],
                                                                  parts: Map[CT, List[RT]],
                                                                  rel: Map[RST, List[RT]]
                                                                  ) {

  assert(mutualDisjoint(List(nt, rt, ct, rst)))
  assert(totalFunction(ct, parts))
  assert(totalFunction(rst, rel))

  def wellformed: Boolean = axiom1 && axiom2 && axiom3 && axiom4 && axiom5

  def axiom1: Boolean =
    all(rt.map(r => any(nt.union(ct).map(t => fills.contains((t, r))))))

  def axiom2: Boolean =
    all(ct.map(c => parts(c).size > 0))

  def axiom3: Boolean = all(rt.map(r => (for (c <- ct if parts(c).contains(r)) yield true).size == 1))

  def axiom4: Boolean =
    all(rst.map(r => rel(r).head != rel(r).tail.head))

  def axiom5: Boolean =
    all(rst.map(r => any(ct.map(c => rel(r).toSet.subsetOf(parts(c).toSet)))))
}
