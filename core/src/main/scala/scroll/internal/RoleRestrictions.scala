package scroll.internal

import java.lang.reflect.Method

import scala.reflect.runtime.universe._
import java.lang

class RoleRestrictions extends ReflectiveHelper {
  private lazy val restrictions = scala.collection.mutable.HashMap.empty[String, Type]

  def addRestriction[A: Manifest, B](implicit tag: WeakTypeTag[B]) {
    restrictions(manifest[A].toString()) = tag.tpe
  }

  private def isInstanceOf(mani: String, that: String) =
    ReflectiveHelper.typeSimpleClassName(that) == ReflectiveHelper.typeSimpleClassName(mani)

  private def matchMethod[A](m: Method, name: String, rType: Type, args: Seq[Type]): Boolean = {
    lazy val matchName = m.getName == name
    lazy val matchParamCount = m.getParameterTypes.length == args.size
    lazy val matchTypes = (args ++ Seq(rType)).zip(m.getParameterTypes ++ Seq(m.getReturnType)).forall {
      case (arg, paramType: Class[_]) => arg.toString match {
        case "Boolean" => paramType == lang.Boolean.TYPE
        case "Char" => paramType == lang.Character.TYPE
        case "Short" => paramType == lang.Short.TYPE
        case "Int" => paramType == lang.Integer.TYPE
        case "Long" => paramType == lang.Long.TYPE
        case "Float" => paramType == lang.Float.TYPE
        case "Double" => paramType == lang.Double.TYPE
        case "Byte" => paramType == lang.Byte.TYPE
        case "Unit" => paramType == lang.Void.TYPE
        case t => isInstanceOf(paramType.toString, t.toString)
      }
    }
    matchName && matchParamCount && matchTypes
  }

  private def isSameInterface(roleInterface: Seq[Method], restrInterface: MemberScope): Boolean = {
    restrInterface.sorted.forall(sy => {
      val mName = sy.asMethod.name.toString
      val rType = sy.asMethod.returnType
      val args = sy.asMethod.paramLists.flatten.map(_.info)
      roleInterface.exists(matchMethod(_, mName, rType, args))
    })
  }

  def validateRoleRestrictions(player: Any, role: Any) = {
    val roleInterface = role.allMethods.toSeq
    restrictions.find { case (pt, rt) =>
      isInstanceOf(pt, player.getClass.toString) && !isSameInterface(roleInterface, rt.decls)
    } match {
      case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt -> $rt'!")
      case None => // fine, thanks
    }
  }
}