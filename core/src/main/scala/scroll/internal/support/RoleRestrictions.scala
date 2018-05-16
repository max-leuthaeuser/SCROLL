package scroll.internal.support

import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

/**
  * Allows to add and check role restrictions (in the sense of structural typing) to a compartment instance.
  */
trait RoleRestrictions {
  private[this] lazy val restrictions = mutable.HashMap.empty[String, mutable.ArrayBuffer[Class[_]]]

  private[this] def addToMap(m: mutable.Map[String, mutable.ArrayBuffer[Class[_]]], elem: (String, Class[_])): Unit = {
    val key = elem._1
    val value = elem._2
    if (m.contains(key)) {
      val _ = m(key) += value
    } else {
      val app = mutable.ArrayBuffer.empty[Class[_]]
      app += value
      val _ = m(key) = app
    }
  }

  /**
    * Add a role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def AddRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag](): Unit = {
    addToMap(restrictions, (classTag[A].toString, classTag[B].runtimeClass))
  }

  /**
    * Replaces a role restriction for a player of type A with a
    * new role restriction between the given player type A and role type B.
    *
    * @tparam A the player type
    * @tparam B the role type
    */
  def ReplaceRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag](): Unit = {
    val app = mutable.ArrayBuffer.empty[Class[_]]
    app += classTag[B].runtimeClass
    restrictions(classTag[A].toString) = app
  }

  /**
    * Removes all role restriction for a player of type A.
    *
    * @tparam A the player type
    */
  def RemoveRoleRestriction[A <: AnyRef : ClassTag](): Unit = {
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
      restrictions.find { case (pt, rts) =>
        ReflectiveHelper.isInstanceOf(pt, player.getClass.toString) && !rts.exists(r => ReflectiveHelper.isSameInterface(roleInterface, r.getDeclaredMethods))
      } match {
        case Some((pt, rt)) => throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions '$pt -> $rt'!")
        case None => // fine, thanks
      }
    }
  }
}