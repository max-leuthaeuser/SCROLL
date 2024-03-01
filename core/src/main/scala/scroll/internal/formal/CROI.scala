package scroll.internal.formal

import scroll.internal.compartment.impl.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag
import scala.reflect.classTag

/** Representation of a Compartment Role Object Instance (CROI).
  */
trait CROI extends CROM {

  protected val croi: FormalCROI[String, String, String, String] =
    FormalCROI.empty[String, String, String, String]

  def compliant(path: String): Boolean = croi.compliant(construct(path))

  private def addType1(of: AnyRef): Unit = {
    val className = of.getClass.toString
    val typeName  = ReflectiveHelper.simpleName(className)
    croi.type1 += (of.hashCode().toString -> typeName)
  }

  def addNatural(n: AnyRef): Unit = {
    croi.n ::= n.hashCode().toString
    addType1(n)
  }

  def addRole(r: AnyRef): Unit = {
    croi.r ::= r.hashCode().toString
    addType1(r)
  }

  def addCompartment[T <: AnyRef: ClassTag](c: T): Unit = {
    require(c.isInstanceOf[Compartment])
    val man      = classTag[T].toString
    val typeName = ReflectiveHelper.simpleName(man)
    croi.c ::= c.hashCode().toString
    croi.type1 += (c.hashCode().toString -> typeName)
  }

  def addPlays(player: AnyRef, comp: AnyRef, role: AnyRef): Unit = {
    val elem = (player.hashCode().toString, comp.hashCode().toString, role.hashCode().toString)
    croi.plays ::= elem
  }

}
