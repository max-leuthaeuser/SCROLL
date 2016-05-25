package scroll.examples

import org.bitbucket.inkytonik.kiama.attribution.Attribution
import org.bitbucket.inkytonik.kiama.relation.Tree
import scroll.internal.Compartment

object RepminKiamaExample extends App with Compartment {

  sealed abstract class RepminTree extends Product

  case class Fork(left: RepminTree, right: RepminTree) extends RepminTree

  case class Leaf(v: Int) extends RepminTree {
    def value: Int = v
  }

  class Repmin(tree: Tree[RepminTree, RepminTree]) extends Attribution {

    private def calc(left: Int, right: Int) = left min right

    val locmin: RepminTree => Int =
      attr {
        case Fork(l, r) => +this calc(locmin(l), locmin(r))
        case l: Leaf => +l value()
      }

    val globmin: RepminTree => Int =
      attr {
        case tree.parent(p) =>
          globmin(p)
        case t =>
          locmin(t)
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
    def value(): Int = (+this).v[Int] * 2
  }

  val in = Fork(Leaf(3), Fork(Leaf(1), Leaf(10)))
  val inWithRole = Fork(Leaf(3), Fork(Leaf(1), Leaf(20) playing new DoubleLeaf()))

  val expectedMin = Fork(Leaf(1), Fork(Leaf(1), Leaf(1)))
  val expectedMax = Fork(Leaf(10), Fork(Leaf(10), Leaf(10)))
  val expectedMaxWithRole = Fork(Leaf(40), Fork(Leaf(40), Leaf(40)))

  val inTree = new Tree[RepminTree, RepminTree](in)
  val inWithRoleTree = new Tree[RepminTree, RepminTree](inWithRole)

  val resultMinFunc: RepminTree => RepminTree = new Repmin(inTree).repmin
  val resultMaxFunc: RepminTree => RepminTree = (new Repmin(inTree) play new Repmax()).repmin
  val resultMaxFuncRole: RepminTree => RepminTree = (new Repmin(inWithRoleTree) play new Repmax()).repmin

  assert(expectedMin == resultMinFunc(in))
  assert(expectedMax == resultMaxFunc(in))
  assert(expectedMaxWithRole == resultMaxFuncRole(inWithRole))
}
