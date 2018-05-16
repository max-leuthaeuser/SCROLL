package scroll.internal.formal

import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag
import scala.reflect.classTag

/**
  * Representation of a Compartment Role Object Instance (CROI).
  */
trait CROI extends CROM {
  protected val croi: FormalCROI[String, String, String, String] = FormalCROI.empty[String, String, String, String]

  def compliant: Boolean = crom.isDefined && croi.compliant(this.crom.get)

  private[this] def addType1(of: AnyRef): Unit = {
    val className = of.getClass.toString
    val typeName = ReflectiveHelper.typeSimpleClassName(className)
    croi.type1 += (ReflectiveHelper.hash(of) -> typeName)
  }

  def addNatural(n: AnyRef): Unit = {
    croi.n ::= ReflectiveHelper.hash(n)
    addType1(n)
  }

  def addRole(r: AnyRef): Unit = {
    croi.r ::= ReflectiveHelper.hash(r)
    addType1(r)
  }

  def addCompartment[T <: AnyRef : ClassTag](c: T): Unit = {
    require(c.isInstanceOf[Compartment])
    val man = classTag[T].toString
    val typeName = ReflectiveHelper.simpleName(man)
    croi.c ::= ReflectiveHelper.hash(c)
    croi.type1 += (ReflectiveHelper.hash(c) -> typeName)
  }

  def addPlays(player: AnyRef, comp: AnyRef, role: AnyRef): Unit = {
    val elem = (ReflectiveHelper.hash(player), ReflectiveHelper.hash(comp), ReflectiveHelper.hash(role))
    croi.plays ::= elem
  }

}
