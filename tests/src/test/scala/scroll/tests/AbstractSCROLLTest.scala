package scroll.tests

import org.scalatest.matchers.should.Matchers
import org.scalatest.ParallelTestExecution
import org.scalatest.funsuite.AnyFunSuite
import scroll.internal.util.ResourceLoader

abstract class AbstractSCROLLTest extends AnyFunSuite with Matchers with ParallelTestExecution {

  protected def streamToSeq(in: java.io.ByteArrayOutputStream, splitAt: String = System.lineSeparator()): Seq[String] =
    in.toString.split(splitAt).toSeq

  protected def resourcePath(name: String): String = ResourceLoader.resourcePath(name)

}
