package scroll.internal.formal

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl
import org.eclipse.emf.ecore.util.EcoreEList
import scroll.internal.ecore.ECoreImporter

import scala.collection.mutable
import scala.jdk.CollectionConverters._

/** Representation of a Compartment Role Object Model (CROM).
  */
trait CROM extends ECoreImporter {
  private val NATURALTYPE     = "NaturalType"
  private val ROLETYPE        = "RoleType"
  private val COMPARTMENTTYPE = "CompartmentType"
  private val ROLEGROUP       = "RoleGroup"
  private val RELATIONSHIP    = "Relationship"
  private val FULFILLMENT     = "Fulfillment"
  private val PART            = "Part"

  private val validTypes =
    Set(NATURALTYPE, ROLEGROUP, ROLETYPE, COMPARTMENTTYPE, RELATIONSHIP, FULFILLMENT, PART)

  /** Checks if the loaded CROM is wellformed.
    *
    * @param path
    *   the file path to load a CROM from
    * @return
    *   true if a model was loaded using `withModel()` and it is wellformed, false otherwise
    */
  def wellformed(path: String): Boolean = construct(path).wellformed

  private def instanceName(of: EObject): String =
    of.eClass()
      .getEAllAttributes
      .asScala
      .find(_.getName == "name")
      .map(of.eGet(_).toString)
      .getOrElse("-")

  private def dynamic(obj: EObject): DynamicEObjectImpl =
    obj.asInstanceOf[DynamicEObjectImpl]

  private def dynamicRoleList(obj: DynamicEObjectImpl, index: Int): List[DynamicEObjectImpl] =
    Option(obj.dynamicGet(index).asInstanceOf[EcoreEList[DynamicEObjectImpl]])
      .map(_.asScala.toList)
      .getOrElse(List.empty)

  private def constructFills(elem: EObject): List[(String, String)] = {
    val obj       = dynamic(elem)
    val filler    = instanceName(dynamic(obj.dynamicGet(1).asInstanceOf[DynamicEObjectImpl]))
    val filledObj = dynamic(obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl])
    if (filledObj.eClass().getName == ROLEGROUP) {
      collectRoles(filledObj).map(r => (filler, instanceName(r)))
    } else {
      val filled = instanceName(dynamic(obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl]))
      List((filler, filled))
    }
  }

  private def collectRoles(of: EObject): List[EObject] =
    of.eContents()
      .asScala
      .toList
      .flatMap(e =>
        e.eClass().getName match {
          case ROLEGROUP => collectRoles(e)
          case ROLETYPE  => List(e)
          case PART      => collectRoles(e)
          case _         => List()
        }
      )

  private def constructParts(elem: EObject): (String, List[String]) = {
    val ct    = instanceName(elem.eContainer())
    val roles = collectRoles(elem).map(instanceName)
    (ct, roles)
  }

  private def constructRel(elem: EObject): (String, List[String]) = {
    val rstName = instanceName(elem)
    val roles   = collectRoles(elem.eContainer())
    val rsts    = roles
      .filter { role =>
        val dynamicRole = dynamic(role)
        val inCond      = dynamicRoleList(dynamicRole, 1).exists(_.dynamicGet(0).asInstanceOf[String] == rstName)
        val outCond     = dynamicRoleList(dynamicRole, 2).exists(_.dynamicGet(0).asInstanceOf[String] == rstName)
        inCond || outCond
      }
      .map(instanceName)
    (rstName, rsts)
  }

  private def addToMap(m: mutable.Map[String, List[String]], elem: (String, List[String])): Unit = {
    val key   = elem._1
    val value = elem._2
    m.update(key, m.getOrElseUpdate(key, value) ++ value)
  }

  private def constructStrings(path: String): FormalCROM[String, String, String, String] = {
    val nt    = mutable.ListBuffer[String]()
    val rt    = mutable.ListBuffer[String]()
    val ct    = mutable.ListBuffer[String]()
    val rst   = mutable.ListBuffer[String]()
    val fills = mutable.ListBuffer[(String, String)]()
    val parts = mutable.Map[String, List[String]]()
    val rel   = mutable.Map[String, List[String]]()

    loadModel(path).getAllContents.asScala
      .filter(e => validTypes.contains(e.eClass().getName))
      .foreach { curr =>
        curr.eClass().getName match {
          case NATURALTYPE     => nt += instanceName(curr)
          case ROLETYPE        => rt += instanceName(curr)
          case COMPARTMENTTYPE => ct += instanceName(curr)
          case RELATIONSHIP    =>
            rst += instanceName(curr)
            addToMap(rel, constructRel(curr))
          case FULFILLMENT => fills ++= constructFills(curr)
          case PART        => addToMap(parts, constructParts(curr))
          case _           =>
        }
      }

    FormalCROM(nt.result(), rt.result(), ct.result(), rst.result(), fills.result(), parts.toMap, rel.toMap)
  }

  protected def construct[NT >: Null <: AnyRef, RT >: Null <: AnyRef, CT >: Null <: AnyRef, RST >: Null <: AnyRef](
    path: String
  ): FormalCROM[NT, RT, CT, RST] =
    constructStrings(path).asInstanceOf[FormalCROM[NT, RT, CT, RST]]

}
