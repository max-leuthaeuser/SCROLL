package scroll.examples

import scroll.internal.Compartment
import org.kiama.attribution.Attribution.attr
import org.kiama.util.TreeNode

object RepminKiamaExample extends App with Compartment {

  sealed abstract class RepminTree extends TreeNode

  case class Fork(left: RepminTree, right: RepminTree) extends RepminTree

  case class Leaf(value: Int) extends RepminTree

  class Repmin {

    private def calc(left: Int, right: Int) = left min right

    val locmin: RepminTree => Int =
      attr {
        case Fork(l, r) => +this calc(locmin(l), locmin(r))
        case l: Leaf => +l value()
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

  class DoubleLeaf() {
    def value: Int = (+this).value[Int] * 2
  }

  val in = Fork(Leaf(3), Fork(Leaf(1), Leaf(10)))
  val inWithRole = Fork(Leaf(3), Fork(Leaf(1), Leaf(20) playing new DoubleLeaf()))

  val expectedMin = Fork(Leaf(1), Fork(Leaf(1), Leaf(1)))
  val expectedMax = Fork(Leaf(10), Fork(Leaf(10), Leaf(10)))
  val expectedMaxWithRole = Fork(Leaf(40), Fork(Leaf(40), Leaf(40)))

  in.initTreeProperties()
  inWithRole.initTreeProperties()

  val resultMinFunc: RepminTree => RepminTree = new Repmin().repmin
  val resultMaxFunc: RepminTree => RepminTree = (new Repmin() play new Repmax()).repmin

  assert(expectedMin == resultMinFunc(in))
  assert(expectedMax == resultMaxFunc(in))
  assert(expectedMaxWithRole == resultMaxFunc(inWithRole))
}
