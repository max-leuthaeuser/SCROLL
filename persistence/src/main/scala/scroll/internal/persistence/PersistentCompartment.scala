package scroll.internal.persistence

import scroll.internal.Compartment
import sorm.Instance

import scala.reflect.runtime.universe._

class PersistentCompartment(db: Instance, factory: TypeFactory) extends Compartment {
  plays = new PersistentScalaRoleGraph(db, factory).load()

  override def addPlaysRelation[C <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](core: C, role: R): Unit = {
    require(null != core)
    require(null != role)
    validate(core, weakTypeOf[R])
    this.plays.addBinding[C, R](core, role)
  }

  override def removePlaysRelation[C <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](core: C, role: R): Unit = {
    require(null != core)
    require(null != role)
    this.plays.removeBinding[C, R](core, role)
  }

  override def transferRole[F <: AnyRef : WeakTypeTag, T <: AnyRef : WeakTypeTag, R <: AnyRef : WeakTypeTag](coreFrom: F, coreTo: T, role: R): Unit = {
    require(null != coreFrom)
    require(null != coreTo)
    require(coreFrom != coreTo, "You can not transfer a role from itself.")
    this.removePlaysRelation(coreFrom, role)
    this.addPlaysRelation(coreTo, role)
  }
}
