package scroll.internal

import java.lang.reflect.Method

import scroll.internal.graph.RoleGraph
import scala.reflect.runtime.universe._
import java.lang

class RoleConstrictions(private val forGraph: RoleGraph[Any]) extends ReflectiveHelper {
  private lazy val restrictions = scala.collection.mutable.HashMap.empty[String, Type]

  def addRestriction[A: Manifest, B](implicit tag: WeakTypeTag[B]) {
    restrictions(manifest[A].toString()) = tag.tpe
  }

  private def isInstanceOf(mani: String, that: Any) =
    ReflectiveHelper.typeSimpleClassName(that.getClass.toString) == ReflectiveHelper.typeSimpleClassName(mani)

  private def matchMethod[A](m: Method, name: String, rType: String, args: List[Type]): Boolean = {
    lazy val matchName = m.getName == name
    lazy val matchRType = ReflectiveHelper.typeSimpleClassName(rType.toLowerCase) == ReflectiveHelper.typeSimpleClassName(m.getReturnType.toString.toLowerCase)
    lazy val matchParamCount = m.getParameterTypes.length == args.size

    lazy val matchArgTypes = args.zip(m.getParameterTypes).forall {
      case (arg@unchecked, paramType: Class[_]) => paramType match {
        case lang.Boolean.TYPE => arg.isInstanceOf[Boolean]
        case lang.Character.TYPE => arg.isInstanceOf[Char]
        case lang.Short.TYPE => arg.isInstanceOf[Short]
        case lang.Integer.TYPE => arg.isInstanceOf[Integer]
        case lang.Long.TYPE => arg.isInstanceOf[Long]
        case lang.Float.TYPE => arg.isInstanceOf[Float]
        case lang.Double.TYPE => arg.isInstanceOf[Double]
        case lang.Byte.TYPE => arg.isInstanceOf[Byte]
        case _ => paramType.isAssignableFrom(arg.getClass)
      }
    }
    matchName && matchRType && matchParamCount && matchArgTypes
  }

  private def isSameInterface(roleInterface: Set[Method], restrInterface: MemberScope): Boolean = {
    val restrList = restrInterface.sorted

    restrList.forall(sy => {
      val mName = sy.asMethod.name.toString
      val rType = sy.asMethod.returnType.toString
      val args = sy.asMethod.paramLists.flatten.map(_.info)
      roleInterface.exists(matchMethod(_, mName, rType, args))
    })
  }

  def validateRoleRestrictions(player: Any, role: Any) = {
    val roleInterface = role.allMethods
    restrictions.find { case (pt, rt) =>
      isInstanceOf(pt, player) && !isSameInterface(roleInterface, rt.decls)
    } match {
      case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt must be played by $rt'!")
      case None => // fine, thanks
    }
  }
}