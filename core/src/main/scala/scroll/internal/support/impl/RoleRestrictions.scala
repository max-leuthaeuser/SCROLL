package scroll.internal.support.impl

import scroll.internal.support.RoleRestrictionsApi
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

class RoleRestrictions() extends RoleRestrictionsApi {
  private[this] lazy val restrictions = mutable.HashMap.empty[String, List[Class[_]]]

  private[this] def addToMap(m: mutable.Map[String, List[Class[_]]], elem: (String, Class[_])): Unit = {
    val key = elem._1
    val value = elem._2
    m.update(key, m.getOrElseUpdate(key, List(value)) :+ value)
  }

  override def addRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag](): Unit = {
    addToMap(restrictions, (classTag[A].toString, classTag[B].runtimeClass))
  }

  override def replaceRoleRestriction[A <: AnyRef : ClassTag, B <: AnyRef : ClassTag](): Unit = {
    restrictions(classTag[A].toString) = List(classTag[B].runtimeClass)
  }

  override def removeRoleRestriction[A <: AnyRef : ClassTag](): Unit = {
    val _ = restrictions.remove(classTag[A].toString)
  }

  override def validate[R <: AnyRef : ClassTag](player: AnyRef, role: R): Unit = {
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
