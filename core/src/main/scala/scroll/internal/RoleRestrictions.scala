package scroll.internal

import scala.collection.mutable
import scala.reflect.runtime.universe._

class RoleRestrictions {
  private lazy val restrictions = mutable.HashMap.empty[String, Type]

  def addRestriction[A: Manifest, B](implicit tag: WeakTypeTag[B]) {
    restrictions(manifest[A].toString()) = tag.tpe
  }

  private def isInstanceOf(mani: String, that: String) =
    ReflectiveHelper.typeSimpleClassName(that) == ReflectiveHelper.typeSimpleClassName(mani)

  private def compareParams(a: List[Symbol], b: List[Symbol]): Boolean = a.size - b.size match {
    case 0 => a.zip(b).forall {
      case (e1, e2) => e1.info =:= e2.info
    }
    case _ => false
  }

  private def isSameInterface(roleInterface: MemberScope, restrInterface: MemberScope): Boolean = {
    restrInterface.sorted.filter(_.isMethod).forall(m => {
      val method = m.asMethod
      roleInterface.sorted.exists {
        case v if v.isMethod => method.name == v.asMethod.name &&
          method.returnType =:= v.asMethod.returnType &&
          compareParams(method.paramLists.flatten, v.asMethod.paramLists.flatten)
        case _ => false
      }
    })
  }

  def validate(player: Any, role: Type) {
    val roleInterface = role.members
    restrictions.find { case (pt, rt) =>
      isInstanceOf(pt, player.getClass.toString) && !isSameInterface(roleInterface, rt.decls)
    } match {
      case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt -> $rt'!")
      case None => // fine, thanks
    }
  }
}