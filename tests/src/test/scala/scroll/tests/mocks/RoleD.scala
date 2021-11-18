package scroll.tests.mocks

class RoleD {
  var valueA: String = "valueA"
  var valueB: Int    = -1

  def i(): Int = 4

  def is(): Seq[Int] = Seq(4, 4)

  def s(): String = "d"

  def update(vA: String, vB: Int): Unit = {
    valueA = vA
    valueB = vB
  }

}
