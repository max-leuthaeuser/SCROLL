package examples

import internal.Compartment
import internal.util.Log.info

import org.kiama.attribution.Attribution.attr
import org.kiama.util.TreeNode

object KiamaExample extends App with Compartment {

  sealed abstract class RepminTree extends TreeNode

  case class Fork(left: RepminTree, right: RepminTree) extends RepminTree

  case class Leaf(value: Int) extends RepminTree

  class Repmin {

    def calc(left: Int, right: Int) = left min right

    val locmin: RepminTree => Int =
      attr {
        case Fork(l, r) => +this calc(locmin(l), locmin(r))
        case Leaf(v) => v
      }

    val globmin: RepminTree => Int =
      attr {
        case t if t.isRoot => locmin(t)
        case t => globmin(t.parent[RepminTree])
      }

    val repmin: RepminTree => RepminTree =
      attr {
        case Fork(l, r) => Fork(repmin(l), repmin(r))
        case t: Leaf => Leaf(globmin(t))
      }
  }

  class Repmax {
    def calc(left: Int, right: Int) = left max right
  }

  val in = Fork(Leaf(3), Fork(Leaf(1), Leaf(10)))
  val min = Fork(Leaf(1), Fork(Leaf(1), Leaf(1)))
  val max = Fork(Leaf(10), Fork(Leaf(10), Leaf(10)))

  in.initTreeProperties()
  val resultMin: RepminTree => RepminTree = new Repmin().repmin
  val resultMax: RepminTree => RepminTree = (new Repmin() play new Repmax()).repmin

  info(resultMin(in).toString)
  info(min.toString)

  info(resultMax(in).toString)
  info(max.toString)

  assert(min == resultMin(in))
  assert(max == resultMax(in))
}
