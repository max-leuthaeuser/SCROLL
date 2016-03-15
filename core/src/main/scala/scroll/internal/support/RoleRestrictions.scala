package scroll.internal.support

import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.runtime.universe._

/**
  * Allows to add and check role restrictions (in the sense of structural typing) to a compartment instance.
  */
trait RoleRestrictions {
  private lazy val restrictions = mutable.HashMap.empty[String, List[Type]]

  private def addToMap(m: mutable.Map[String, List[Type]], elem: (String, List[Type])): Unit = {
    val key = elem._1
    val value = elem._2
    if (m.contains(key)) {
      m(key) = m(key) ++ value
    } else {
      val _ = m += elem
    }
  }

  private def isInstanceOf(mani: String, that: String): Boolean =
    ReflectiveHelper.typeSimpleClassName(that) == ReflectiveHelper.typeSimpleClassName(mani)

  private def compareParams(a: List[Symbol], b: List[Symbol]): Boolean = a.size - b.size match {
    case 0 => a.zip(b).forall {
      case (e1, e2) => e1.info =:= e2.info || e1.info <:< e2.info
    }
    case _ => false
  }

  private def isSameInterface(roleInterface: MemberScope, restrInterface: MemberScope): Boolean = {
    restrInterface.sorted.filter(_.isMethod).forall(m => {
      val method = m.asMethod
      roleInterface.sorted.exists {
        case v if v.isMethod =>
          method.name == v.asMethod.name &&
            (method.returnType =:= v.asMethod.returnType || method.returnType <:< v.asMethod.returnType) &&
            compareParams(method.paramLists.flatten, v.asMethod.paramLists.flatten)
        case _ => false
      }
    })
  }

  /**
    * Add a role restriction between the given player type A and role type B.
    *
    * @param tag implicitly added WeakTypeTag for the role type
    * @tparam A the player type
    * @tparam B the role type
    */
  def RoleRestriction[A: WeakTypeTag, B](implicit tag: WeakTypeTag[B]): Unit = {
    addToMap(restrictions, (weakTypeOf[A].toString, List(tag.tpe)))
  }

  /**
    * Replaces a role restriction for a player of type A with a
    * new role restriction between the given player type A and role type B.
    *
    * @param tag implicitly added WeakTypeTag for the role type
    * @tparam A the player type
    * @tparam B the role type
    */
  def ReplaceRoleRestriction[A: WeakTypeTag, B](implicit tag: WeakTypeTag[B]): Unit = {
    restrictions(weakTypeOf[A].toString) = List(tag.tpe)
  }

  /**
    * Checks all role restriction between the given player and a role type.
    * Will throw a RuntimeException if a restriction is violated!
    *
    * @param player the player instance to check
    * @param role   the role type to check
    */
  protected def validate(player: Any, role: Type): Unit = {
    val roleInterface = role.members
    restrictions.find { case (pt, rts) =>
      isInstanceOf(pt, player.getClass.toString) && !rts.exists(r => isSameInterface(roleInterface, r.decls))
    } match {
      case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt -> $rt'!")
      case None => // fine, thanks
    }
  }
}