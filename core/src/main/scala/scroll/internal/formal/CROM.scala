package scroll.internal.formal

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl
import org.eclipse.emf.ecore.util.EcoreEList
import scroll.internal.ecore.ECoreImporter

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Representation of a Compartment Role Object Model (CROM).
  */
trait CROM extends ECoreImporter {
  private val NATURALTYPE = "NaturalType"
  private val ROLETYPE = "RoleType"
  private val COMPARTMENTTYPE = "CompartmentType"
  private val ROLEGROUP = "RoleGroup"
  private val RELATIONSHIP = "Relationship"
  private val FULFILLMENT = "Fulfillment"
  private val PART = "Part"

  private val validTypes = Set(NATURALTYPE, ROLEGROUP, ROLETYPE, COMPARTMENTTYPE, RELATIONSHIP, FULFILLMENT, PART)

  protected var crom = Option.empty[FormalCROM[String, String, String, String]]

  /**
    * Load and replace the current model instance.
    *
    * @param path the file path to load a CROM from
    */
  def withModel(path: String) {
    require(null != path && path.nonEmpty)
    this.path = path
    crom = Option(construct())
  }

  /**
    * Checks if the loaded CROM is wellformed.
    *
    * @return true if a model was loaded using `withModel()` and it is wellformed, false otherwise
    */
  def wellformed: Boolean = crom.isDefined && crom.forall(_.wellformed)

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
    if (filledObj.eClass().getName == ROLEGROUP) {
      collectRoles(filledObj).map(r => (filler, getInstanceName(r).asInstanceOf[RT]))
    } else {
      val filled = obj.dynamicGet(0).asInstanceOf[DynamicEObjectImpl].dynamicGet(0).asInstanceOf[RT]
      List((filler, filled))
    }
  }

  private def collectRoles(of: EObject): List[EObject] = of.eContents().toList.flatMap(e => e.eClass().getName match {
    case ROLEGROUP => collectRoles(e)
    case ROLETYPE => List(e)
    case PART => collectRoles(e)
    case _ => List()
  })

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
      val inCond = incoming match {
        case null => false
        case _ => incoming.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
      }
      val outgoing = role.asInstanceOf[DynamicEObjectImpl].dynamicGet(2).asInstanceOf[EcoreEList[DynamicEObjectImpl]]
      val outCond = outgoing match {
        case null => false
        case _ => outgoing.exists(e => e.dynamicGet(0).asInstanceOf[String] == rstName)
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

  private def construct[NT >: Null, RT >: Null, CT >: Null, RST >: Null](): FormalCROM[NT, RT, CT, RST] = {
    val nt = ListBuffer[String]()
    val rt = ListBuffer[String]()
    val ct = ListBuffer[String]()
    val rst = ListBuffer[String]()
    val fills = ListBuffer[(String, String)]()
    val parts = mutable.Map[String, List[String]]()
    val rel = mutable.Map[String, List[String]]()

    loadModel().getAllContents.filter(e => validTypes.contains(e.eClass().getName)).foreach(curr => {
      curr.eClass().getName match {
        case NATURALTYPE => nt += constructNT(curr)
        case ROLETYPE => rt += constructRT(curr)
        case COMPARTMENTTYPE => ct += constructCT(curr)
        case RELATIONSHIP =>
          rst += constructRST[String](curr)
          addToMap(rel, constructRel[String, String](curr))
        case FULFILLMENT => fills ++= constructFills(curr)
        case PART => addToMap(parts, constructParts[String, String](curr))
        case _ =>
      }
    })
    FormalCROM(nt.result(), rt.result(), ct.result(), rst.result(), fills.result(), parts.toMap, rel.toMap).asInstanceOf[FormalCROM[NT, RT, CT, RST]]
  }
}
