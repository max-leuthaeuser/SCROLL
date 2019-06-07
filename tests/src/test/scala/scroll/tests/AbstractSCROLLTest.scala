package scroll.tests

import org.scalatest.Matchers
import org.scalatest.funsuite.AnyFunSuite

abstract class AbstractSCROLLTest extends AnyFunSuite with Matchers {

  protected def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

}
