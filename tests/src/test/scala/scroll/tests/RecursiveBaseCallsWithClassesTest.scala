package scroll.tests

import org.junit.Test
import org.junit.Assert.assertArrayEquals

import scroll.internal.Compartment
import scroll.internal.support.DispatchQuery
import scroll.internal.support.DispatchQuery._

class RecursiveBaseCallsWithClassesTest {

  import scala.collection.JavaConverters._

  class CoreType {
    def someMethod(): Unit = {
      println(s"CoreType($this)::someMethod()")
    }
  }

  class MultiRole extends Compartment {

    class RoleTypeA {
      implicit val dd: DispatchQuery = Bypassing((o: AnyRef) => {
        o == this || !o.isInstanceOf[CoreType]
      })

      def someMethod(): Unit = {
        println(s"RoleTypeA($this)::someMethod()")
        (+this).someMethod()
      }
    }

    class RoleTypeB {
      implicit val dd: DispatchQuery = Bypassing(_ == this)

      def someMethod(): Unit = {
        println(s"RoleTypeB($this)::someMethod()")
        (+this).someMethod()
      }
    }

  }

  private def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

  @Test
  def testDispatchingNormalCalls(): Unit = {
    new MultiRole() {
      val c = new CoreType()
      val r = new RoleTypeA()
      val player = c play r
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        player.someMethod()
      }
      val actual = streamToSeq(output)
      val expected = Seq(
        s"RoleTypeA($r)::someMethod()",
        s"CoreType($c)::someMethod()"
      )
      assertArrayEquals(expected.asJava.toArray, actual.asJava.toArray)
    }
  }

  @Test
  def testDispatchingRecursiveCalls(): Unit = {
    new MultiRole() {
      val c1 = new CoreType()
      val c2 = new CoreType()
      val rA1 = new RoleTypeA()
      val rA2 = new RoleTypeA()
      val rB = new RoleTypeB()
      val player1 = c1 play rA1
      rA1 play rB
      val player2 = c2 play rA2
      val output = new java.io.ByteArrayOutputStream()
      Console.withOut(output) {
        player1.someMethod()
      }
      val actual = streamToSeq(output)
      val expected = Seq(
        s"RoleTypeB($rB)::someMethod()",
        s"RoleTypeA($rA1)::someMethod()",
        s"CoreType($c1)::someMethod()"
      )
      assertArrayEquals(expected.asJava.toArray, actual.asJava.toArray)
    }
  }
}