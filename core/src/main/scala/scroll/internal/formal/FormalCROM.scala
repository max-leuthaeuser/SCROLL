package scroll.internal.formal

/**
  * Companion object for the formal representation of the Compartment Role Object Model (CROM).
  */
object FormalCROM {
  def empty[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef]: FormalCROM[NT, RT, CT, RST] =
    FormalCROM[NT, RT, CT, RST](List.empty, List.empty, List.empty, List.empty, List.empty, Map.empty, Map.empty)

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
                ): FormalCROM[String, String, String, String] =
    FormalCROM(nt, rt, ct, rst, fills, parts, rel)
}

/**
  * Class representation of the Compartment Role Object Model (CROM).
  *
  * @param nt    list of all natural types
  * @param rt    list of all role types
  * @param ct    list of all compartment types
  * @param rst   list of all relationship types
  * @param fills fills-relation
  * @param parts parts-relation
  * @param rel   relationship mappings
  * @tparam NT  type of naturals
  * @tparam RT  type of roles
  * @tparam CT  type of compartments
  * @tparam RST type of relationships
  */
case class FormalCROM[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef](
                                                                                                                nt: List[NT],
                                                                                                                rt: List[RT],
                                                                                                                ct: List[CT],
                                                                                                                rst: List[RST],
                                                                                                                fills: List[(NT, RT)],
                                                                                                                parts: Map[CT, List[RT]],
                                                                                                                rel: Map[RST, List[RT]]
                                                                                                              ) {

  assert(FormalUtils.mutualDisjoint(List(nt, rt, ct, rst)))
  assert(FormalUtils.totalFunction(ct, parts))
  assert(FormalUtils.totalFunction(rst, rel))

  /**
    * @return true iff the CROM is wellformed w.r.t. all axioms provided, false otherwise
    */
  def wellformed: Boolean = axiom1 && axiom2 && axiom3 && axiom4 && axiom5

  def axiom1: Boolean =
    FormalUtils.all(rt.map(r => FormalUtils.any(nt.union(ct).map(t => fills.contains((t, r))))))

  def axiom2: Boolean =
    FormalUtils.all(ct.map(c => parts(c).nonEmpty))

  def axiom3: Boolean = FormalUtils.all(rt.map(r => (for (c <- ct if parts(c).contains(r)) yield true).size == 1))

  def axiom4: Boolean =
    FormalUtils.all(rst.map(r => rel(r).head != rel(r).tail.head))

  def axiom5: Boolean =
    FormalUtils.all(rst.map(r => FormalUtils.any(ct.map(c => rel(r).toSet.subsetOf(parts(c).toSet)))))
}
