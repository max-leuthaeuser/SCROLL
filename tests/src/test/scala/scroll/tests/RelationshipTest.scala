package scroll.tests

import org.junit.Test
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.fail

import scroll.internal.util.Many._
import mocks.{CoreA, SomeCompartment}

class RelationshipTest {

  import scala.collection.JavaConverters._

  @Test
  def testRelationships(): Unit = {
    val p = new CoreA
    new SomeCompartment() {
      val rA = new RoleA
      val rB = new RoleB
      val rC = new RoleC
      p play rA play rB

      val rel1 = Relationship("rel1").from[RoleA](1).to[RoleB](1)

      assertArrayEquals(rel1.left().asJava.toArray, Seq(rA).asJava.toArray)
      assertArrayEquals(rel1.right().asJava.toArray, Seq(rB).asJava.toArray)

      val rel2 = Relationship("rel2").from[RoleA](1).to[RoleC](1)

      assertArrayEquals(rel2.left().asJava.toArray, Seq(rA).asJava.toArray)
      try {
        rel2.right()
        fail("Should throw an AssertionError")
      } catch {
        case _: AssertionError => // all good
      }

      val rel3 = Relationship("rel3").from[RoleA](1).to[RoleB](*)
      assertArrayEquals(rel3.left().asJava.toArray, Seq(rA).asJava.toArray)
      assertArrayEquals(rel3.right().asJava.toArray, Seq(rB).asJava.toArray)
      val rB2 = new RoleB
      p play rB2
      assertArrayEquals(rel3.right().asJava.toArray, Seq(rB, rB2).asJava.toArray)
      val rB3 = new RoleB
      p play rB3
      assertArrayEquals(rel3.right().asJava.toArray, Seq(rB, rB2, rB3).asJava.toArray)
    }
  }
}
