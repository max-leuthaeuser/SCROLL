package scroll.internal.formal

/** Companion object for the formal representation of the Compartment Role Object Instance (CROI).
  */
object FormalCROI {

  def empty[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef]
    : FormalCROI[NT, RT, CT, RST] =
    FormalCROI[NT, RT, CT, RST](List.empty, List.empty, List.empty, Map.empty, List.empty, Map.empty)

  /** Little helper factory method for creating a CROI with Strings only.
    */
  def forStrings(
    n: List[String],
    r: List[String],
    c: List[String],
    type1: Map[AnyRef, AnyRef],
    plays: List[(String, String, String)],
    links: Map[(String, String), List[(String, String)]]
  ): FormalCROI[String, String, String, String] = FormalCROI(n, r, c, type1, plays, links)

}

/** Class representation of the Compartment Role Object Instance (CROI).
  *
  * @param n
  *   list of all naturals
  * @param r
  *   list of all roles
  * @param c
  *   list of all compartments
  * @param type1
  *   type mapping
  * @param plays
  *   plays relation
  * @param links
  *   link function
  * @tparam NT
  *   type of naturals
  * @tparam RT
  *   type of roles
  * @tparam CT
  *   type of compartments
  * @tparam RST
  *   type of relationships
  */
final case class FormalCROI[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef](
  var n: List[NT],
  var r: List[RT],
  var c: List[CT],
  var type1: Map[AnyRef, AnyRef],
  var plays: List[(NT, CT, RT)],
  var links: Map[(RST, CT), List[(RT, RT)]]
) {

  assert(FormalUtils.mutualDisjoint(List(n, r, c, List(null))))

  assert(FormalUtils.totalFunction(n.concat(r).concat(c), type1.map { case (k, v) => (k, List(v)) }))

  /** @param crom
    *   the CROM to check against
    * @return
    *   true iff the CROI is compliant to the given CROM
    */
  def compliant(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    crom.wellformed &&
      axiom6(crom) && axiom7() && axiom8() &&
      axiom9(crom) && axiom10(crom) && axiom11(crom)

  def axiom6(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(plays.map { case (o, c1, r1) =>
      crom.fills.contains((type1(o), type1(r1))) && crom
        .parts(type1(c1).asInstanceOf[CT])
        .contains(type1(r1))
    })

  def axiom7(): Boolean =
    FormalUtils.all(for {
      (o, c, r)    <- plays
      (o1, c1, r1) <- plays if o1 == o && c1 == c && r1 != r
    } yield type1(r1) != type1(r))

  def axiom8(): Boolean =
    FormalUtils.all(r.map(r1 => plays.collect { case (o, cc, r2) if r2 == r1 => (o, cc) }.lengthCompare(1) == 0))

  def axiom9(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for {
      c1 <- c
      r1 <- crom.rst if links.contains((r1, c1))
    } yield !links((r1, c1)).contains((null, null)))

  def axiom10(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(
      for {
        rst1 <- crom.rst
        c1   <- c if links.contains((rst1, c1))
        r1   <- r
        o1   <- o
      } yield FormalUtils.any(
        for {
          r_1 <- repsilon
        } yield ((plays
          .contains((o1, c1, r1)) && (type1(r1) == crom.rel(rst1).head)) == links((rst1, c1))
          .contains((r1, r_1))) &&
          ((plays
            .contains((o1, c1, r1)) && (type1(r1) == crom.rel(rst1).tail.head)) == links((rst1, c1))
            .contains((r_1, r1)))
      )
    )

  def axiom11(crom: FormalCROM[NT, RT, CT, RST]): Boolean =
    FormalUtils.all(for {
      rst1       <- crom.rst
      c1         <- c if links.contains((rst1, c1))
      (r_1, r_2) <- links((rst1, c1)) if r_1 != null && r_2 != null
    } yield !links((rst1, c1)).contains((r_1, null)) && !links((rst1, c1)).contains((null, r_2)))

  private def o: List[AnyRef] = n.concat(c)

  def o_c(c: CT): List[NT] = plays.filter(_._2 == c).map(_._1)

  private def repsilon: List[RT] = r :+ null

  def pred(rst: RST, c: CT, r: RT): List[RT] =
    if (links.contains((rst, c))) {
      links((rst, c)).filter(_._2 == r).map(_._1)
    } else {
      List.empty[RT]
    }

  def succ(rst: RST, c: CT, r: RT): List[RT] =
    if (links.contains((rst, c))) {
      links((rst, c)).filter(_._1 == r).map(_._2)
    } else {
      List.empty[RT]
    }

  private def player(r: RT): NT =
    r match {
      case null => null
      case _    =>
        plays.find(_._3 == r) match {
          case Some(p) => p._1
          case _       => throw new RuntimeException(s"The given role '$r' is not played in the CROI!")
        }
    }

  def overline_links(rst: RST, c: CT): List[(NT, NT)] =
    links((rst, c)).map { case (r_1, r_2) =>
      (player(r_1), player(r_2))
    }

}
