import scala.language.postfixOps
import collection.JavaConversions._
import com.tinkerpop.gremlin.process.Path
import com.tinkerpop.gremlin.process.Traverser
import com.tinkerpop.gremlin.scala._
import org.scalatest._
import scala.util.Success
import scala.util.Failure
import java.net.ConnectException
import InMemoryConnect._
import scala.concurrent.Await
import scala.concurrent.duration._

// calculate the shortest path while travelling New Zealand
// inspired by Stefan Bleibinhaus
// http://bleibinha.us/blog/2013/10/scala-and-graph-databases-with-gremlin-scala
class ShortestPathGramlinScalaSpec extends FlatSpec
  with Matchers
  with InMemoryConnect
  with SilentLogging
  with DefaultExecutionContext {

  "Gremlin-Scala" should "connect to Titan database and find the shortest path between two vertices" in {
    val cFuture = connect()
    cFuture onComplete {
      case Success(c) =>
        c.isOpen shouldBe true
      case Failure(t) => throw new ConnectException(t.getMessage)
    }

    val gsTry = Await.ready(cFuture, Duration.Inf).value.get
    val gs = gsTry match {
      case Success(g) => GremlinScala(g)
      case Failure(t) => throw new Exception(t.getMessage)
    }

    def addLocation(name: String): ScalaVertex =
      gs.addVertex().setProperty("name", name)

    def addRoad(from: ScalaVertex, to: ScalaVertex, distance: Int): Unit = {
      // two way road ;)
      from.addEdge(label = "road", to, Map.empty).setProperty("distance", distance)
      to.addEdge(label = "road", from, Map.empty).setProperty("distance", distance)
    }

    val auckland = addLocation("Auckland")
    val whangarei = addLocation("Whangarei")
    val dargaville = addLocation("Dargaville")
    val kaikohe = addLocation("Kaikohe")
    val kerikeri = addLocation("Kerikeri")
    val kaitaia = addLocation("Kaitaia")
    val capeReinga = addLocation("Cape Reinga")

    addRoad(auckland, whangarei, 158)
    addRoad(whangarei, kaikohe, 85)
    addRoad(kaikohe, kaitaia, 82)
    addRoad(kaitaia, capeReinga, 111)
    addRoad(whangarei, kerikeri, 85)
    addRoad(kerikeri, kaitaia, 88)
    addRoad(auckland, dargaville, 175)
    addRoad(dargaville, kaikohe, 77)
    addRoad(kaikohe, kerikeri, 36)

    val paths = auckland.as("a").outE.inV.jump(
      to = "a",
      jumpPredicate = { t: Traverser[Vertex] ⇒
        t.loops < 6 &&
          t.get.value[String]("name") != "Cape Reinga" &&
          t.get.value[String]("name") != "Auckland"
      }).filter(_.value[String]("name") == "Cape Reinga").path.toList

    case class DescriptionAndDistance(description: String, distance: Int)

    val descriptionAndDistances: List[DescriptionAndDistance] = paths map { p: Path ⇒
      val pathDescription = p.objects collect {
        case v: Vertex ⇒ v.value[String]("name")
      } mkString " -> "

      val pathTotalKm = p.objects collect {
        case e: Edge => e.value[Int]("distance")
      } sum

      DescriptionAndDistance(pathDescription, pathTotalKm)
    }

    println(s"Paths from Auckland to Cape Reinga:")
    descriptionAndDistances foreach println
    descriptionAndDistances.size shouldBe 23

    val shortestPath = descriptionAndDistances.sortBy(_.distance).head
    shortestPath.distance shouldBe 436
    shortestPath.description shouldBe "Auckland -> Whangarei -> Kaikohe -> Kaitaia -> Cape Reinga"

    println("shortest path: " + shortestPath)

    gs.close()
  }
}