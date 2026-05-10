package scroll.tests.other

import scroll.internal.errors.SCROLLErrors.ReflectiveFieldNotFound
import scroll.internal.errors.SCROLLErrors.ReflectiveMethodNotFound
import scroll.internal.util.ReflectiveHelper
import scroll.tests.AbstractSCROLLTest

class ReflectiveHelperTest extends AbstractSCROLLTest {

  test("findMethod prefers the most specific overload for a subclass argument") {
    val output = new java.io.ByteArrayOutputStream()
    val target = new java.io.PrintStream(output)
    val method = ReflectiveHelper.findMethod(target, "print", Seq("value"))

    method.map(_.getParameterTypes.toSeq) shouldBe Some(Seq(classOf[String]))
    ReflectiveHelper.resultOf[Unit](target, method.get, Seq("value"))
    output.toString shouldBe "value"
  }

  test("findMethod prefers the most specific reference overload for null arguments") {
    class Overloaded {
      def dispatch(value: CharSequence): String = "char-sequence"
      def dispatch(value: String): String       = "string"
    }

    val target = new Overloaded()
    val method = ReflectiveHelper.findMethod(target, "dispatch", Seq(null))

    method.map(_.getParameterTypes.toSeq) shouldBe Some(Seq(classOf[String]))
    ReflectiveHelper.resultOf[String](target, method.get, Seq(null)) shouldBe "string"
  }

  test("findMethod matches primitive parameters against boxed Scala arguments") {
    class PrimitiveTarget {
      def increment(value: Int): Int = value + 1
    }

    val target = new PrimitiveTarget()
    val method = ReflectiveHelper.findMethod(target, "increment", Seq(1))

    method.map(_.getParameterTypes.toSeq) shouldBe Some(Seq(java.lang.Integer.TYPE))
    ReflectiveHelper.resultOf[Int](target, method.get, Seq(1)) shouldBe 2
  }

  test("propertyOf reports typed missing-field errors") {
    class Target {
      val value: String = "ok"
    }

    val err = intercept[ReflectiveFieldNotFound] {
      ReflectiveHelper.propertyOf[String](new Target(), "missing")
    }

    err.fieldName shouldBe "missing"
  }

  test("resultOf reports typed missing-method errors") {
    class Target {
      def value: String = "ok"
    }

    val err = intercept[ReflectiveMethodNotFound] {
      ReflectiveHelper.resultOf[String](new Target(), "missing")
    }

    err.methodName shouldBe "missing"
  }

}
