package scroll.tests

import org.jgrapht.graph.DefaultEdge
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import scroll.internal.persistence.{DBConnection, PersistentTypes, TypeFactory, PersistentCompartment}
import sorm.{InitMode, Entity}
import scala.collection.JavaConverters._

case class PersistentPlayer(name: String)

case class PersistentRoleA(name: String)

case class PersistentRoleB(name: String)

class PersistenceTest extends FeatureSpec with GivenWhenThen with Matchers {
  info("Test spec for persistence.")

  feature("PersistentCompartments") {

    val db = new DBConnection(
      entities = Set(
        Entity[PersistentPlayer](),
        Entity[PersistentRoleA](),
        Entity[PersistentRoleB]()),
      url = "jdbc:h2:mem:test",
      initMode = InitMode.DropAllCreate)

    @PersistentTypes("PersistentPlayer", "PersistentRoleA", "PersistentRoleB")
    class Factory extends TypeFactory

    val factory = new Factory()

    var c1Plays: java.util.Set[DefaultEdge] = null
    var c2Plays: java.util.Set[DefaultEdge] = null

    val player1 = db.save(PersistentPlayer("P1"))
    val player2 = db.save(PersistentPlayer("P2"))
    val roleA = db.save(PersistentRoleA("RA"))
    val roleB = db.save(PersistentRoleB("RB"))

    scenario("play") {
      new PersistentCompartment(db, factory) {
        player1 play roleA
        player1 play roleB
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }

    scenario("drop") {
      new PersistentCompartment(db, factory) {
        player1 play roleA
        player1 play roleB
        player1 drop roleB
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }

    scenario("transfer") {
      new PersistentCompartment(db, factory) {
        player1 play roleA
        player2 play roleB
        player1 transfer roleA to player2
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }
  }
}
