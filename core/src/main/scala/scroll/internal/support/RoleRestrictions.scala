package scroll.internal.support

import java.lang.reflect.Method

import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.{ClassTag, classTag}

/**
  * Allows to add and check role restrictions (in the sense of structural typing) to a compartment instance.
  */
trait RoleRestrictions {
  private lazy val restrictions = mutable.HashMap.empty[String, List[Class[_]]]

  private def addToMap(m: mutable.Map[String, List[Class[_]]], elem: (String, List[Class[_]])): Unit = {
    val key = elem._1
    val value = elem._2
    if (m.contains(key)) {
      m(key) = m(key) ++ value
    } else {
      val _ = m += elem
    }
  }

  private def isInstanceOf(mani: String, that: String): Boolean =
    ReflectiveHelper.simpleName(that) == ReflectiveHelper.simpleName(mani)

  private def isSameInterface(roleInterface: Array[Method], restrInterface: Array[Method]): Boolean =
    restrInterface.forall(method => roleInterface.exists(method.equals))

  /**
    * Add a role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def RoleRestriction[A: ClassTag, B: ClassTag](): Unit = {
    addToMap(restrictions, (classTag[A].toString, List(classTag[B].runtimeClass)))
  }

  /**
    * Replaces a role restriction for a player of type A with a
    * new role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def ReplaceRoleRestriction[A: ClassTag, B: ClassTag](): Unit = {
    restrictions(classTag[A].toString) = List(classTag[B].runtimeClass)
  }

  /**
    * Checks all role restriction between the given player and a role type.
    * Will throw a RuntimeException if a restriction is violated!
    *
    * @param player the player instance to check
    * @param role   the role type to check
    */
  protected def validate[R: ClassTag](player: Any, role: R): Unit = {
    val roleInterface = classTag[R].runtimeClass.getDeclaredMethods
    restrictions.find { case (pt, rts) =>
      isInstanceOf(pt, player.getClass.toString) && !rts.exists(r => isSameInterface(roleInterface, r.getDeclaredMethods))
    } match {
      case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt -> $rt'!")
      case None => // fine, thanks
    }
  }
}