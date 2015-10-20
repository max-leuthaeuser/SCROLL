package scroll.internal.formal

/**
 * Companion object for the formal representation of the constraint model.
 */
object FormalConstraintModel {
  def empty[NT >: Null, RT >: Null, CT >: Null, RST >: Null]: FormalConstraintModel[NT, RT, CT, RST] = FormalConstraintModel[NT, RT, CT, RST](Map.empty, Map.empty, List.empty)

  /**
   * Little helper factory method for creating a constraint model with Strings only.
   */
  def forStrings(rolec: Map[String, List[((Int, Int), Any)]],
                 card: Map[String, ((Int, Int), (Int, Int))],
                 intra: List[(String, (List[(String, String)]) => Boolean)]): FormalConstraintModel[String, String, String, String] = FormalConstraintModel(rolec, card, intra)
}

/**
 * Class representation of the Constraint Model.
 *
 * @param rolec the role constraints
 * @param card cardinality mappings
 * @param intra intra-relationship constraints
 * @tparam NT type of naturals
 * @tparam RT type of roles
 * @tparam CT type of compartments
 * @tparam RST type of relationships
 */
case class FormalConstraintModel[NT >: Null, RT >: Null, CT >: Null, RST >: Null](rolec: Map[CT, List[((Int, Int), Any)]],
                                                                                  card: Map[RST, ((Int, Int), (Int, Int))],
                                                                                  intra: List[(RST, (List[(NT, NT)]) => Boolean)]) {

  /**
   * @param crom the CROM instance to check against
   * @return true iff the constraint model is compliant to the given CROM.
   */
  def compliant(crom: FormalCROM[NT, RT, CT, RST]): Boolean = crom.wellformed && axiom12(crom)

  def axiom12(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for (ct1 <- crom.ct if rolec.contains(ct1); (crd, a) <- rolec(ct1)) yield
    FormalUtils.atoms(a).toSet.subsetOf(crom.parts(ct1).toSet)
    )

  /**
   * @param crom the CROM instance to check against
   * @param croi the CROI instance to check against
   * @return true iff the constraint model is compliant to the given CROM and the given CROI is valid wrt. the constraint model
   */
  def validity(crom: FormalCROM[NT, RT, CT, RST], croi: FormalCROI[NT, RT, CT, RST]): Boolean = compliant(crom) && croi.compliant(crom) &&
    axiom13(crom, croi) && axiom14(crom, croi) && axiom15(crom, croi) && axiom16(crom, croi)

  def axiom13(crom: FormalCROM[NT, RT, CT, RST], croi: FormalCROI[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for (ct1 <- crom.ct if rolec.contains(ct1); (crd, a) <- rolec(ct1); c1 <- croi.c if croi.type1(c1) == ct1) yield {
      val sum = croi.o_c(c1).map(FormalUtils.evaluate(a, croi, _, c1)).sum
      crd._1 <= sum && sum <= crd._2
    }
    )

  def axiom14(crom: FormalCROM[NT, RT, CT, RST], croi: FormalCROI[NT, RT, CT, RST]): Boolean =
  // TODO: fix asInstanceOf
    FormalUtils.all(for ((o, c, r) <- croi.plays if rolec.contains(croi.type1(c).asInstanceOf[CT]); (crd, a) <- rolec(croi.type1(c).asInstanceOf[CT]) if FormalUtils.atoms(a).contains(croi.type1(r))) yield
    FormalUtils.evaluate(a, croi, o, c) == 1
    )

  def axiom15(crom: FormalCROM[NT, RT, CT, RST], croi: FormalCROI[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for (rst <- crom.rst if card.contains(rst); c <- croi.c if croi.links.contains(rst, c); (r_1, r_2) <- croi.links((rst, c))) yield {
      val l1 = croi.pred(rst, c, r_2).size
      val l2 = croi.succ(rst, c, r_1).size
      card(rst)._1._1 <= l1 && l1 <= card(rst)._1._2 && card(rst)._2._1 <= l2 && l2 <= card(rst)._2._2
    }
    )

  def axiom16(crom: FormalCROM[NT, RT, CT, RST], croi: FormalCROI[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for (c <- croi.c; (rst, f) <- intra if croi.links.contains((rst, c))) yield f(croi.overline_links(rst, c)))
}