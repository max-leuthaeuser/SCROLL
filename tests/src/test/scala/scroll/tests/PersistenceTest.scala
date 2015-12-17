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

    val player1 = PersistentPlayer("P1")
    val player2 = PersistentPlayer("P2")
    val roleA = PersistentRoleA("RA")
    val roleB = PersistentRoleB("RB")

    val p_player1 = db.save(player1)
    val p_player2 = db.save(player2)
    val p_roleA = db.save(roleA)
    val p_roleB = db.save(roleB)

    scenario("play") {
      new PersistentCompartment(db, factory) {
        p_player1 play p_roleA
        p_player1 play p_roleB
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
        player1.isPlaying[PersistentRoleA] shouldBe true
        player1.isPlaying[PersistentRoleB] shouldBe true
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }

    scenario("drop") {
      new PersistentCompartment(db, factory) {
        p_player1 play p_roleA
        p_player1 play p_roleB
        p_player1 drop p_roleB
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
        player1.isPlaying[PersistentRoleA] shouldBe true
        player1.isPlaying[PersistentRoleB] shouldBe false
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }

    scenario("transfer") {
      new PersistentCompartment(db, factory) {
        p_player1 play p_roleA
        p_player2 play p_roleB
        p_player1 transfer p_roleA to p_player2
        c1Plays = plays.store.edgeSet()
      }

      new PersistentCompartment(db, factory) {
        c2Plays = plays.store.edgeSet()
        player1.isPlaying[PersistentRoleA] shouldBe false
        player1.isPlaying[PersistentRoleB] shouldBe false
        player2.isPlaying[PersistentRoleA] shouldBe true
        player2.isPlaying[PersistentRoleB] shouldBe true
      }

      c1Plays.size() shouldBe c2Plays.size()
      c1Plays.asScala.toSeq zip c2Plays.asScala.toSeq foreach {
        case (e1, e2) => e1.toString shouldBe e2.toString
      }
    }
  }
}
