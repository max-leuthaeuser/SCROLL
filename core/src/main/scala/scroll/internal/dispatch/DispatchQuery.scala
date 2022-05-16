package scroll.internal.dispatch

/** Companion object for [[DispatchQuery]] providing some static dispatch functions and a fluent dispatch query creation
  * API.
  */
object DispatchQuery {

  type Selector = AnyRef => Boolean

  type SelectorFunction = Seq[AnyRef] => Seq[AnyRef]

  /** Use this in [[DispatchQuery.sortedWith]] to state that no sorting between the objects in comparison should happen.
    */
  val identity: Boolean = false

  /** Use this in [[DispatchQuery.sortedWith]] to state that always swapping between the objects in comparison should
    * happen.
    */
  val swap: Boolean = true

  /** Function to use in [[DispatchQuery.sortedWith]] to simply reverse the set of resulting edges.
    */
  val reverse: PartialFunction[(AnyRef, AnyRef), Boolean] = { case (_, _) =>
    swap
  }

  /** Function always returning true
    */
  val anything: Selector = _ => true

  /** Function always returning false
    */
  val nothing: Selector = _ => false

  private def empty: DispatchQuery =
    new DispatchQuery(new From(anything), new To(anything), new Through(anything), new Bypassing(nothing))

  /** Dispatch filter selecting the sub-path from the starting edge until the end of the path given as Seq, w.r.t. the
    * evaluation of the selection function.
    *
    * @param sel
    *   the selection function to evaluate on each element of the path
    */
  case class From(val sel: Selector) extends SelectorFunction {

    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] =
      edges.slice(edges.indexWhere(sel), edges.size)

  }

  /** Dispatch filter selecting the sub-path from the last edge until the end of the path given as Seq, w.r.t. the
    * evaluation of the selection function.
    *
    * @param sel
    *   the selection function to evaluate on each element of the path
    */
  case class To(val sel: Selector) extends SelectorFunction {

    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] =
      edges.lastIndexWhere(sel) match {
        case -1 => edges
        case _  => edges.slice(0, edges.lastIndexWhere(sel) + 1)
      }

  }

  /** Dispatch filter to specify which edges to keep on the path given as Seq, w.r.t. the evaluation of the selection
    * function.
    *
    * @param sel
    *   the selection function to evaluate on each element of the path
    */
  case class Through(sel: Selector) extends SelectorFunction {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = edges.filter(sel)
  }

  /** Dispatch filter to specify which edges to skip on the path given as Seq, w.r.t. the evaluation of the selection
    * function.
    *
    * @param sel
    *   the selection function to evaluate on each element of the path
    */
  case class Bypassing(sel: Selector) extends SelectorFunction {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = edges.filterNot(sel)
  }

  def apply(): DispatchQuery = empty

  extension (dd: DispatchQuery) {
    infix def From(sel: Selector): DispatchQuery      = dd.copy(from = new From(sel))
    infix def To(sel: Selector): DispatchQuery        = dd.copy(to = new To(sel))
    infix def Through(sel: Selector): DispatchQuery   = dd.copy(through = new Through(sel))
    infix def Bypassing(sel: Selector): DispatchQuery = dd.copy(bypassing = new Bypassing(sel))
  }

  given Conversion[From, DispatchQuery]      = f => DispatchQuery().From(f.sel)
  given Conversion[To, DispatchQuery]        = f => DispatchQuery().To(f.sel)
  given Conversion[Through, DispatchQuery]   = f => DispatchQuery().Through(f.sel)
  given Conversion[Bypassing, DispatchQuery] = b => DispatchQuery().Bypassing(b.sel)

}

import scroll.internal.dispatch.DispatchQuery._

/** Composed dispatch query, i.e., applying the composition of all dispatch queries the given set of edges. All provided
  * queries must be side-effect free!
  *
  * @param from
  *   query selecting the starting element for the role dispatch query
  * @param to
  *   query selecting the end element for the role dispatch query
  * @param through
  *   query specifying intermediate elements for the role dispatch query
  * @param bypassing
  *   query specifying all elements to be left out for the role dispatch query
  */
case class DispatchQuery(
  from: From,
  to: To,
  through: Through,
  bypassing: Bypassing,
  private[this] var _sortedWith: Option[(AnyRef, AnyRef) => Boolean] = Option.empty
) {

  /** Set the function to later sort all dynamic extensions during [[DispatchQuery.filter]].
    *
    * @param f
    *   the sorting function
    * @return
    *   this
    */
  def sortedWith(f: PartialFunction[(AnyRef, AnyRef), Boolean]): DispatchQuery = {
    _sortedWith = Option { case (a, b) =>
      f.applyOrElse((a, b), (_: (AnyRef, AnyRef)) => identity)
    }
    this
  }

  /** Applies the composition filters and sorting function to the given set of objects.
    *
    * @param anys
    *   The Seq of objects to filter and sort
    * @return
    *   the filtered and sorted Seq of objects
    */
  def filter(anys: Seq[AnyRef]): Seq[AnyRef] = {
    val r = from.andThen(to).andThen(through).andThen(bypassing)(anys).reverse
    _sortedWith.fold(r)(s => r.sortWith(s))
  }

}
