package scroll.tests.mocks

class RoleA {
  val valueA: String = "valueA"
  var valueB: Int = 1
  var valueC: String = "valueC"

  def i(): Int = 1

  def is(): Seq[Int] = Seq(1, 1)

  def s(): String = "a"

  def a(): Int = 0

  def b(a: String, param: String = "in"): String = param

  def update(value: String): Unit = {
    this.valueC = value
  }

}
