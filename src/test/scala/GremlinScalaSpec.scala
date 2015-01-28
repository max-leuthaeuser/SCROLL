
import com.tinkerpop.gremlin.process.T
import com.tinkerpop.gremlin.scala.GremlinScala
import org.scalatest._
import scala.util.Success
import scala.util.Failure
import com.thinkaurelius.titan.core.TitanGraph
import java.net.ConnectException
import InMemoryConnect._
import scala.concurrent.Await
import scala.concurrent.duration._

class GremlinScalaSpec extends FlatSpec
  with Matchers
  with InMemoryConnect
  with DefaultLogging
  with DefaultExecutionContext {

  "Gremlin-Scala" should "connect to Titan database and pull out Saturn's keys and shutdown cleanly" in {
    val cFuture = connect()
    cFuture onComplete {
      case Success(c) =>
        c.isOpen() shouldBe true
      case Failure(t) => throw new ConnectException(t.getMessage)
    }

    val gsTry = Await.ready(cFuture, Duration.Inf).value.get
    val gs = gsTry match {
      case Success(g) => GremlinScala(g)
      case Failure(t) => throw new Exception(t.getMessage)
    }

    (1 to 5) foreach { i ⇒
      gs.addVertex().setProperty("name", s"vertex $i")
    }
    gs.addVertex("saturn", Map("name" -> "saturn"))

    gs.V.count().head shouldBe 6

    val traversal = gs.V.value[String]("name")
    traversal.toList.size shouldBe 6

    gs.V.has(T.label, "saturn").count().head shouldBe 1

    val saturnQ = gs.V.has(T.label, "saturn").head
    saturnQ.property[String]("name").value shouldBe "saturn"

    gs.close
  }
}