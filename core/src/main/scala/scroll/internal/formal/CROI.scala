package scroll.internal.formal

import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.reflect.runtime.universe._

/**
  * Representation of a Compartment Role Object Instance (CROI).
  */
trait CROI extends CROM {
  protected val croi = FormalCROI.empty[String, String, String, String]

  def compliant: Boolean = crom.isDefined && croi.compliant(this.crom.get)

  private def addType1(of: Any): Unit = {
    val className = of.getClass.toString
    val typeName = ReflectiveHelper.typeSimpleClassName(className)
    croi.type1 += (ReflectiveHelper.hash(of) -> typeName)
  }

  def addNatural(n: Any): Unit = {
    croi.n ::= ReflectiveHelper.hash(n)
    addType1(n)
  }

  def addRole(r: Any): Unit = {
    croi.r ::= ReflectiveHelper.hash(r)
    addType1(r)
  }

  def addCompartment[T: WeakTypeTag](c: T): Unit = {
    require(c.isInstanceOf[Compartment])
    val man = weakTypeOf[T].toString
    val typeName = man.substring(man.indexOf(".") + 1, man.lastIndexOf(" with"))
    croi.c ::= ReflectiveHelper.hash(c)
    croi.type1 += (ReflectiveHelper.hash(c) -> typeName)
  }

  def addPlays(player: Any, comp: Any, role: Any): Unit = {
    val elem = (ReflectiveHelper.hash(player), ReflectiveHelper.hash(comp), ReflectiveHelper.hash(role))
    croi.plays ::= elem
  }

}
