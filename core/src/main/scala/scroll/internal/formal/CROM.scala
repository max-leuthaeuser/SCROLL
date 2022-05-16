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
  private[this] val NATURALTYPE     = "NaturalType"
  private[this] val ROLETYPE        = "RoleType"
  private[this] val COMPARTMENTTYPE = "CompartmentType"
  private[this] val ROLEGROUP       = "RoleGroup"
  private[this] val RELATIONSHIP    = "Relationship"
  private[this] val FULFILLMENT     = "Fulfillment"
  private[this] val PART            = "Part"

  private[this] val validTypes =
    Set(NATURALTYPE, ROLEGROUP, ROLETYPE, COMPARTMENTTYPE, RELATIONSHIP, FULFILLMENT, PART)

  /** Checks if the loaded CROM is wellformed.
    *
    * @param path
    *   the file path to load a CROM from
    * @return
    *   true if a model was loaded using `withModel()` and it is wellformed, false otherwise
    */
  def wellformed(path: String): Boolean = construct(path).wellformed

  private[this] def instanceName(of: EObject): String =
    of.eClass()
      .getEAllAttributes
      .asScala
      .find(_.getName == "name")
      .map(of.eGet(_).toString)
      .getOrElse("-")

  private[this] def constructNT[NT >: Null <: AnyRef](elem: EObject): NT =
    instanceName(elem).asInstanceOf[NT]

  private[this] def constructRT[RT >: Null <: AnyRef](elem: EObject): RT =
    instanceName(elem).asInstanceOf[RT]

  private[this] def constructCT[CT >: Null <: AnyRef](elem: EObject): CT =
    instanceName(elem).asInstanceOf[CT]

  private[this] def constructRST[RST >: Null <: AnyRef](elem: EObject): RST =
    instanceName(elem).asInstanceOf[RST]

  private[this] def constructFills[NT >: Null <: AnyRef, RT >: Null <: AnyRef](elem: EObject): List[(NT, RT)] = {
    val obj       = elem.asInstanceOf[DynamicEObjectImpl]
    val filler    = obj.dynamicGet(1).asInstanceOf[DynamicEObjectImpl].dynamicGet(0).asInstanceOf[NT]
    val filledObj = obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl]
    if (filledObj.eClass().getName == ROLEGROUP) {
      collectRoles(filledObj).map(r => (filler, instanceName(r).asInstanceOf[RT]))
    } else {
      val filled = obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl].dynamicGet(0).asInstanceOf[RT]
      List((filler, filled))
    }
  }

  private[this] def collectRoles(of: EObject): List[EObject] =
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

  private[this] def constructParts[CT >: Null <: AnyRef, RT >: Null <: AnyRef](elem: EObject): (CT, List[RT]) = {
    val ct    = instanceName(elem.eContainer()).asInstanceOf[CT]
    val roles = collectRoles(elem).map(r => instanceName(r).asInstanceOf[RT])
    (ct, roles)
  }

  private[this] def constructRel[RST >: Null <: AnyRef, RT >: Null <: AnyRef](elem: EObject): (RST, List[RT]) = {
    val rstName = instanceName(elem)
    val roles   = collectRoles(elem.eContainer())
    val rsts = roles
      .filter { role =>
        val incoming = role
          .asInstanceOf[DynamicEObjectImpl]
          .dynamicGet(1)
          .asInstanceOf[EcoreEList[DynamicEObjectImpl]]
          .asScala
        val inCond = incoming match {
          case null => false
          case _    => incoming.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
        }
        val outgoing = role
          .asInstanceOf[DynamicEObjectImpl]
          .dynamicGet(2)
          .asInstanceOf[EcoreEList[DynamicEObjectImpl]]
          .asScala
        val outCond = outgoing match {
          case null => false
          case _    => outgoing.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
        }
        inCond || outCond
      }
      .map(instanceName(_).asInstanceOf[RT])
    (rstName.asInstanceOf[RST], rsts)
  }

  private[this] def addToMap(m: mutable.Map[String, List[String]], elem: (String, List[String])): Unit = {
    val key   = elem._1
    val value = elem._2
    m.update(key, m.getOrElseUpdate(key, value) ++ value)
  }

  protected[this] def construct[
    NT >: Null <: AnyRef,
    RT >: Null <: AnyRef,
    CT >: Null <: AnyRef,
    RST >: Null <: AnyRef
  ](path: String): FormalCROM[NT, RT, CT, RST] = {
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
          case NATURALTYPE     => nt += constructNT(curr)
          case ROLETYPE        => rt += constructRT(curr)
          case COMPARTMENTTYPE => ct += constructCT(curr)
          case RELATIONSHIP =>
            val _ = rst += constructRST[String](curr)
            addToMap(rel, constructRel[String, String](curr))
          case FULFILLMENT => fills ++= constructFills(curr)
          case PART        => addToMap(parts, constructParts[String, String](curr))
          case _           =>
        }
      }
    FormalCROM(nt.result(), rt.result(), ct.result(), rst.result(), fills.result(), parts.toMap, rel.toMap)
      .asInstanceOf[FormalCROM[NT, RT, CT, RST]]
  }

}
