package scroll.internal.support

import scroll.internal.support.DispatchQuery.Bypassing
import scroll.internal.support.DispatchQuery.From
import scroll.internal.support.DispatchQuery.Through
import scroll.internal.support.DispatchQuery.To
import scroll.internal.support.DispatchQuery.identity

/**
  * Companion object for [[scroll.internal.support.DispatchQuery]] providing
  * some static dispatch functions and a fluent dispatch query creation API.
  */
object DispatchQuery {

  /**
    * Use this in [[DispatchQuery.sortedWith]] to state that no sorting between the objects in comparison should happen.
    */
  val identity: Boolean = false

  /**
    * Use this in [[DispatchQuery.sortedWith]] to state that always swapping between the objects in comparison should happen.
    */
  val swap: Boolean = true

  /**
    * Function to use in [[DispatchQuery.sortedWith]] to simply reverse the set of resulting edges.
    */
  val reverse: PartialFunction[(AnyRef, AnyRef), Boolean] = {
    case (_, _) => swap
  }

  /**
    * Function always returning true
    */
  val anything: AnyRef => Boolean = _ => true
  /**
    * Function always returning false
    */
  val nothing: AnyRef => Boolean = _ => false

  protected class ToBuilder(f: AnyRef => Boolean) {
    def To(t: AnyRef => Boolean): ThroughBuilder = new ThroughBuilder(f, t)
  }

  protected class ThroughBuilder(f: AnyRef => Boolean, t: AnyRef => Boolean) {
    def Through(th: AnyRef => Boolean): BypassingBuilder = new BypassingBuilder(f, t, th)
  }

  protected class BypassingBuilder(f: AnyRef => Boolean, t: AnyRef => Boolean, th: AnyRef => Boolean) {
    def Bypassing(b: AnyRef => Boolean): DispatchQuery =
      new DispatchQuery(new From(f), new To(t), new Through(th), new Bypassing(b))
  }

  def From(f: AnyRef => Boolean): ToBuilder = new ToBuilder(f)

  def Bypassing(b: AnyRef => Boolean): DispatchQuery =
    new DispatchQuery(new From(anything, empty = true), new To(anything, empty = true), new Through(anything, empty = true), new Bypassing(b))

  def empty: DispatchQuery = new DispatchQuery(new From(anything), new To(anything), new Through(anything), new Bypassing(nothing), empty = true)

  /**
    * Dispatch filter selecting the sub-path from the starting edge until the end
    * of the path given as Seq, w.r.t. the evaluation of the selection function.
    *
    * @param sel   the selection function to evaluate on each element of the path
    * @param empty if set to true, the path will be returned unmodified
    */
  class From(val sel: AnyRef => Boolean, empty: Boolean = false) extends (Seq[AnyRef] => Seq[AnyRef]) {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = if (empty) {
      edges
    } else {
      edges.slice(edges.indexWhere(sel), edges.size)
    }
  }

  /**
    * Dispatch filter selecting the sub-path from the last edge until the end
    * of the path given as Seq, w.r.t. the evaluation of the selection function.
    *
    * @param sel   the selection function to evaluate on each element of the path
    * @param empty if set to true, the path will be returned unmodified
    */
  class To(val sel: AnyRef => Boolean, empty: Boolean = false) extends (Seq[AnyRef] => Seq[AnyRef]) {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = if (empty) {
      edges
    } else {
      edges.lastIndexWhere(sel) match {
        case -1 => edges
        case _ => edges.slice(0, edges.lastIndexWhere(sel) + 1)
      }
    }
  }

  /**
    * Dispatch filter to specify which edges to keep on the path given as Seq,
    * w.r.t. the evaluation of the selection function.
    *
    * @param sel   the selection function to evaluate on each element of the path
    * @param empty if set to true, the path will be returned unmodified
    */
  class Through(sel: AnyRef => Boolean, empty: Boolean = false) extends (Seq[AnyRef] => Seq[AnyRef]) {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = if (empty) {
      edges
    } else {
      edges.filter(sel)
    }
  }

  /**
    * Dispatch filter to specify which edges to skip on the path given as Seq,
    * w.r.t. the evaluation of the selection function.
    *
    * @param sel   the selection function to evaluate on each element of the path
    * @param empty if set to true, the path will be returned unmodified
    */
  class Bypassing(sel: AnyRef => Boolean, empty: Boolean = false) extends (Seq[AnyRef] => Seq[AnyRef]) {
    override def apply(edges: Seq[AnyRef]): Seq[AnyRef] = if (empty) {
      edges
    } else {
      edges.filterNot(sel)
    }
  }

}

/**
  * Composed dispatch query, i.e., applying the composition of all dispatch queries the given set of edges.
  * All provided queries must be side-effect free!
  *
  * @param from      query selecting the starting element for the role dispatch query
  * @param to        query selecting the end element for the role dispatch query
  * @param through   query specifying intermediate elements for the role dispatch query
  * @param bypassing query specifying all elements to be left out for the role dispatch query
  */
class DispatchQuery(
                     from: From,
                     to: To,
                     through: Through,
                     bypassing: Bypassing,
                     private[this] val empty: Boolean = false,
                     private[this] var _sortedWith: Option[(AnyRef, AnyRef) => Boolean] = Option.empty
                   ) {
  def isEmpty: Boolean = empty

  /**
    * Set the function to later sort all dynamic extensions during [[DispatchQuery.filter]].
    *
    * @param f the sorting function
    * @return this
    */
  def sortedWith(f: PartialFunction[(AnyRef, AnyRef), Boolean]): DispatchQuery = {
    _sortedWith = Some({ case (a, b) => f.applyOrElse((a, b), (_: (AnyRef, AnyRef)) => identity) })
    this
  }

  /**
    * Applies the composition filters and sorting function to the given set of objects.
    *
    * @param anys The Seq of objects to filter and sort
    * @return the filtered and sorted Seq of objects
    */
  def filter(anys: Seq[AnyRef]): Seq[AnyRef] = {
    val r = if (isEmpty) {
      anys.reverse
    } else {
      from.andThen(to).andThen(through).andThen(bypassing)(anys).reverse
    }
    _sortedWith.fold(r) { s => r.sortWith(s) }
  }

}
