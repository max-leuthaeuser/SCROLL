package scroll.internal.persistence

import scroll.internal.graph.ScalaRoleGraph
import scroll.internal.persistence.PersistentScalaRoleGraph.Plays
import scroll.internal.util.ReflectiveHelper
import sorm.Dsl._
import sorm.{Instance, Persisted}

import scala.reflect.runtime.universe._

object PersistentScalaRoleGraph {

  case class Plays(playerID: Long, playerType: String, roleID: Long, roleType: String)

}

class PersistentScalaRoleGraph(db: Instance, factory: TypeFactory, checkForCycles: Boolean = true) extends ScalaRoleGraph(checkForCycles) {

  private def checkType[T <: AnyRef : WeakTypeTag](o: T): T = o match {
    case _: Persisted => o
    case _ => throw new RuntimeException("You need to persist '" + o + "' first, otherwise playing, dropping or transferring roles will not work!")
  }

  private def weakToTypeString(in: String): String = ReflectiveHelper.typeSimpleClassName(in.substring(0, in.indexOf(" with")))

  def load(): PersistentScalaRoleGraph = {
    db.transaction {
      db.query[Plays].fetch().foreach(p => {
        val playerID = p.playerID
        val roleID = p.roleID
        val playerType = p.playerType
        val roleType = p.roleType
        val player = factory.create(playerID, playerType, db)
        val role = factory.create(roleID, roleType, db)
        require(null != player)
        require(null != role)
        store.addVertex(player)
        store.addVertex(role)
        store.addEdge(player, role)
      })
    }
    this
  }

  override def addBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    val _ = db.transaction {
      val playerID = db.save(checkType(player)).id
      val playerType = weakToTypeString(weakTypeOf[P].toString)
      val roleID = db.save(checkType(role)).id
      val roleType = weakToTypeString(weakTypeOf[R].toString)
      val plays = Plays(playerID, playerType, roleID, roleType)
      db.save(plays)
      val p = factory.create(playerID, playerType, db)
      val r = factory.create(roleID, roleType, db)
      store.addVertex(p)
      store.addVertex(r)
      store.addEdge(p, r)
    }
  }

  override def removeBinding[P <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](player: P, role: R): Unit = {
    require(null != player)
    require(null != role)
    val _ = db.transaction {
      val playerID = db.save(checkType(player)).id
      val playerType = weakToTypeString(weakTypeOf[P].toString)
      val roleID = db.save(checkType(role)).id
      val roleType = weakToTypeString(weakTypeOf[R].toString)
      db.query[Plays]
        .where(
          ("playerID" equal playerID)
            and ("roleID" equal roleID)
            and ("playerType" equal playerType)
            and ("roleType" equal roleType)
        ).fetch().foreach(r => db.delete(r))
      val p = factory.create(playerID, playerType, db)
      val r = factory.create(roleID, roleType, db)
      store.removeEdge(p, r)
    }
  }

  override def removePlayer[P <: AnyRef : WeakTypeTag](player: P): Unit = {
    require(null != player)
    val _ = db.transaction {
      val playerID = db.save(checkType(player)).id
      val playerType = weakToTypeString(weakTypeOf[P].toString)
      val p = factory.create(playerID, playerType, db)
      db.delete(player)
      store.removeVertex(p)
    }
  }
}
