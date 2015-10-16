package scroll.internal.formal

import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

trait CROI extends CROM {
  protected val croi = FormalCROI.empty[String, String, String, String]

  def compliant: Boolean = crom.isDefined && croi.compliant(this.crom.get)

  private def addType1(of: Any) {
    val className = of.getClass.toString
    val typeName = className.contains("$") match {
      case false => ReflectiveHelper.typeSimpleClassName(className)
      case true => ReflectiveHelper.classSimpleClassName(className)
    }
    croi.type1 += (ReflectiveHelper.hash(of) -> typeName)
  }

  def addNatural(n: Any) {
    croi.n ::= ReflectiveHelper.hash(n)
    addType1(n)
  }

  def addRole(r: Any) {
    croi.r ::= ReflectiveHelper.hash(r)
    addType1(r)
  }

  def addCompartment[T: Manifest](c: T) {
    require(c.isInstanceOf[Compartment])
    val man = manifest[T].toString()
    val typeName = man.substring(man.indexOf(".") + 1, man.lastIndexOf(" with"))
    croi.c ::= ReflectiveHelper.hash(c)
    croi.type1 += (ReflectiveHelper.hash(c) -> typeName)
  }

  def addPlays(player: Any, comp: Any, role: Any) {
    val elem = (ReflectiveHelper.hash(player), ReflectiveHelper.hash(comp), ReflectiveHelper.hash(role))
    croi.plays ::= elem
  }

}
