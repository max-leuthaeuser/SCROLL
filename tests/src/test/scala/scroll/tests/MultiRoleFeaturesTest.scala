package scroll.tests

import org.junit.Test
import org.junit.Assert.fail
import org.junit.Assert.assertArrayEquals

import scroll.internal.MultiCompartment
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._
import scroll.tests.mocks.CoreA

class MultiRoleFeaturesTest {

  @Test
  def testRoleMethodInvokation(): Unit = {
    import scala.collection.JavaConverters._

    val someCore = new CoreA()

    case class RoleA(id: String = "RoleA")

    case class RoleB(id: String = "RoleB")

    case class RoleC(id: String = "RoleC")

    new MultiCompartment() {
      implicit var dd: DispatchQuery = DispatchQuery.empty.sortedWith {
        case (_: RoleC, _: RoleA) => swap
        case (_: RoleB, _: RoleA) => swap
        case (_: RoleC, _: RoleB) => swap
      }
      val roleA = RoleA()
      val roleB = RoleB()
      val roleC = RoleC()
      someCore play roleA play roleB play roleC

      val expected = Seq(Right("RoleC"), Right("RoleB"), Right("RoleA"))
      +someCore id() match {
        case Right(actual) => assertArrayEquals(actual.asJava.toArray, expected.asJava.toArray)
        case Left(error) => fail(error.toString)
      }

      dd = DispatchQuery.empty.sortedWith {
        case (_: RoleA, _: RoleC) => swap
        case (_: RoleA, _: RoleB) => swap
        case (_: RoleB, _: RoleC) => swap
      }
      +someCore id() match {
        case Right(actual) => assertArrayEquals(actual.asJava.toArray, expected.reverse.asJava.toArray)
        case Left(error) => fail(error.toString)
      }


      val expected2 = Seq(Right("RoleC"), Right("RoleB"))
      dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
        case (_: RoleC, _: RoleB) => swap
      }
      +someCore id() match {
        case Right(actual) => assertArrayEquals(actual.asJava.toArray, expected2.asJava.toArray)
        case Left(error) => fail(error.toString)
      }

      dd = Bypassing(_.isInstanceOf[RoleA]).sortedWith {
        case (_: RoleB, _: RoleC) => swap
      }
      +someCore id() match {
        case Right(actual) => assertArrayEquals(actual.asJava.toArray, expected2.reverse.asJava.toArray)
        case Left(error) => fail(error.toString)
      }
    }
  }
}