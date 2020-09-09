package scroll.internal.support

import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

/**
  * Allows to add and check role restrictions (in the sense of structural typing) to a compartment instance.
  */
trait RoleRestrictions {
  private[this] lazy val restrictions = mutable.HashMap.empty[String, List[Class[_]]]

  private[this] def addToMap(m: mutable.Map[String, List[Class[_]]], elem: (String, Class[_])): Unit = {
    val key = elem._1
    val value = elem._2
    m.update(key, m.getOrElseUpdate(key, List(value)) :+ value)
  }

  /**
    * Add a role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def AddRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag]: Unit = {
    addToMap(restrictions, (classTag[A].toString, classTag[B].runtimeClass))
  }

  /**
    * Replaces a role restriction for a player of type A with a
    * new role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def ReplaceRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag]: Unit = {
    restrictions(classTag[A].toString) = List(classTag[B].runtimeClass)
  }

  /**
    * Removes all role restriction for a player of type A.
    *
    * @tparam A the player type
    */
  def RemoveRoleRestriction[A <: AnyRef : ClassTag]: Unit = {
    val _ = restrictions.remove(classTag[A].toString)
  }

  /**
    * Checks all role restriction between the given player and a role type.
    * Will throw a RuntimeException if a restriction is violated!
    *
    * @param player the player instance to check
    * @param role   the role type to check
    */
  protected def validate[R <: AnyRef : ClassTag](player: AnyRef, role: R): Unit = {
    if (restrictions.nonEmpty) {
      val roleInterface = classTag[R].runtimeClass.getDeclaredMethods
      if (restrictions.exists { case (pt, rts) =>
        ReflectiveHelper.isInstanceOf(pt, player.getClass.toString) && !rts.exists(r => ReflectiveHelper.isSameInterface(roleInterface, r.getDeclaredMethods))
      }) {
        throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions!")
      }
    }
  }
}
