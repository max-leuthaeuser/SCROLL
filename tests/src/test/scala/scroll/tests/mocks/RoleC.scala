package scroll.tests.mocks

import scroll.internal.support.UnionTypes.RoleUnionTypes

class RoleC extends RoleUnionTypes {

  def i(): Int = 3

  def is(): Seq[Int] = Seq(3, 3)

  def s(): String = "c"

  def unionTypedMethod[T: (Int or String)#λ](param: T): Int = param match {
    case i: Int => i
    case s: String => s.length
  }

}
