package scroll.internal.formal

import scroll.internal.Compartment
import scroll.internal.ReflectiveHelper._


trait CROIInstance extends CROMInstance {

  protected val croi = CROI.empty[String, String, String, String]

  def compliant: Boolean = crom.isDefined && croi.compliant(this.crom.get)

  private def addType1(of: Any) {
    val className = of.getClass.toString
    val typeName = className.contains("$") match {
      case false => typeSimpleClassName(className)
      case true => classSimpleClassName(className)
    }
    croi.type1 += (hash(of) -> typeName)
  }

  def addNatural(n: Any) {
    croi.n ::= hash(n)
    addType1(n)
  }

  def addRole(r: Any) {
    croi.r ::= hash(r)
    addType1(r)
  }

  def addCompartment[T: Manifest](c: T) {
    require(c.isInstanceOf[Compartment])
    val man = manifest[T].toString()
    val typeName = man.substring(man.indexOf(".") + 1, man.lastIndexOf(" with"))
    croi.c ::= hash(c)
    croi.type1 += (hash(c) -> typeName)
  }

  def addPlays(player: Any, comp: Any, role: Any) {
    val elem = (hash(player), hash(comp), hash(role))
    croi.plays ::= elem
  }

}
