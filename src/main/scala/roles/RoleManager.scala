package roles

import scala.collection.mutable.{HashMap, MultiMap}
import scala.collection.mutable

// TODO: make this a compartment
object RoleManager
{
  val plays = new HashMap[Any, mutable.Set[Any]]() with MultiMap[Any, Any]
  {
    override def default
    (key: Any) = mutable.Set.empty
  }

  def addPlaysRelation(
    core: Any,
    role: Any)
  {
    plays.addBinding(core, role)
  }

  def removePlaysRelation(
    core: Any,
    role: Any)
  {
    plays.removeBinding(core, role)
  }

  def transferRole(
    coreFrom: Any,
    coreTo: Any,
    role: Any)
  {
    assert(coreFrom != coreTo)
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  def transferRoles(
    coreFrom: Any,
    coreTo: Any,
    roles: Set[Any])
  {
    roles.foreach(transferRole(coreFrom, coreTo, _))
  }

  def getRelation(core: Any): mutable.Set[Any] = plays(core)

  def getCoreFor(role: Any): Any = plays.foreach {
    case (
      k,
      v) => if (v.contains(role)) return k
  }
}
