package scroll.internal.ecore

import org.eclipse.emf.ecore.impl.DynamicEObjectImpl
import org.eclipse.emf.ecore.util.EcoreEList
import org.eclipse.emf.ecore.EObject
import scroll.internal.formal.CROM
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

trait CROMInstance extends ECoreImporter {
  private val validTypes = Set("NaturalType", "RoleType", "CompartmentType", "RoleGroup", "Relationship", "Fulfillment", "Part")

  private def getInstanceName(of: EObject): String = of.eClass().getEAllAttributes.find(_.getName == "name") match {
    case Some(a) => of.eGet(a).toString
    case None => "-"
  }

  private def constructNT[NT >: Null](elem: EObject): NT = getInstanceName(elem).asInstanceOf[NT]

  private def constructRT[RT >: Null](elem: EObject): RT = getInstanceName(elem).asInstanceOf[RT]

  private def constructCT[CT >: Null](elem: EObject): CT = getInstanceName(elem).asInstanceOf[CT]

  private def constructRST[RST >: Null](elem: EObject): RST = getInstanceName(elem).asInstanceOf[RST]

  private def constructFills[NT >: Null, RT >: Null](elem: EObject): List[(NT, RT)] = {
    val obj = elem.asInstanceOf[DynamicEObjectImpl]
    val filler = obj.dynamicGet(1).asInstanceOf[DynamicEObjectImpl].dynamicGet(0).asInstanceOf[NT]
    val filledObj = obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl]
    if (filledObj.eClass().getName == "RoleGroup") {
      collectRoles(filledObj).map(r => (filler, getInstanceName(r).asInstanceOf[RT]))
    } else {
      val filled = obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl].dynamicGet(0).asInstanceOf[RT]
      List((filler, filled))
    }
  }

  private def collectRoles(of: EObject): List[EObject] = of.eContents().toList.map(e => e.eClass().getName match {
    case "RoleGroup" => collectRoles(e)
    case "RoleType" => List(e)
    case "Part" => collectRoles(e)
    case _ => List()
  }).flatten

  private def constructParts[CT >: Null, RT >: Null](elem: EObject): (CT, List[RT]) = {
    val ct = getInstanceName(elem.eContainer()).asInstanceOf[CT]
    val roles = collectRoles(elem).map(r => getInstanceName(r).asInstanceOf[RT])
    (ct, roles)
  }

  private def constructRel[RST >: Null, RT >: Null](elem: EObject): (RST, List[RT]) = {
    val rstName = getInstanceName(elem).asInstanceOf[RST]
    val roles = collectRoles(elem.eContainer())
    // TODO: make sure order of roles (incoming/outgoing) is correct for the given relationship
    val rsts = roles.filter(role => {
      val incoming = role.asInstanceOf[DynamicEObjectImpl].dynamicGet(1).asInstanceOf[EcoreEList[DynamicEObjectImpl]]
      var inCond = false
      if (incoming != null) {
        inCond = incoming.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
      }
      val outgoing = role.asInstanceOf[DynamicEObjectImpl].dynamicGet(2).asInstanceOf[EcoreEList[DynamicEObjectImpl]]
      var outCond = false
      if (outgoing != null) {
        outCond = outgoing.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
      }
      inCond || outCond
    }).map(getInstanceName(_).asInstanceOf[RT])
    (rstName, rsts)
  }

  private def addToMap(m: mutable.Map[String, List[String]], elem: (String, List[String])) {
    val key = elem._1
    val value = elem._2
    if (m.contains(key)) {
      m(key) = m(key) ++ value
    } else {
      m += elem
    }
  }

  def construct[NT >: Null, RT >: Null, CT >: Null, RST >: Null](): CROM[NT, RT, CT, RST] = {
    val nt = ListBuffer[String]()
    val rt = ListBuffer[String]()
    val ct = ListBuffer[String]()
    val rst = ListBuffer[String]()
    val fills = ListBuffer[(String, String)]()
    val parts = mutable.Map[String, List[String]]()
    val rel = mutable.Map[String, List[String]]()

    loadModel().getAllContents.filter(e => validTypes.contains(e.eClass().getName)).foreach(curr => {
      curr.eClass().getName match {
        case "NaturalType" => nt += constructNT(curr)
        case "RoleType" => rt += constructRT(curr)
        case "CompartmentType" => ct += constructCT(curr)
        case "Relationship" =>
          rst += constructRST[String](curr)
          addToMap(rel, constructRel[String, String](curr))
        case "Fulfillment" => fills ++= constructFills(curr)
        case "Part" => addToMap(parts, constructParts[String, String](curr))
        case _ =>
      }
    })
    CROM(nt.result(), rt.result(), ct.result(), rst.result(), fills.result(), parts.toMap, rel.toMap).asInstanceOf[CROM[NT, RT, CT, RST]]
  }
}
