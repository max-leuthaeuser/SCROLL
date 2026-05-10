package scroll.internal.support.impl

import scroll.internal.support.RoleRestrictionsApi
import scroll.internal.util.ReflectiveHelper

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag
import scala.reflect.classTag

class RoleRestrictions() extends RoleRestrictionsApi {
  private lazy val restrictions = TrieMap.empty[String, List[Class[?]]]

  @tailrec
  private def addToMap(key: String, value: Class[?]): Unit =
    restrictions.get(key) match {
      case Some(existing) =>
        if (!restrictions.replace(key, existing, existing :+ value)) {
          addToMap(key, value)
        }
      case None =>
        if (restrictions.putIfAbsent(key, List(value)).nonEmpty) {
          addToMap(key, value)
        }
    }

  override def addRoleRestriction[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit =
    addToMap(classTag[A].toString, classTag[B].runtimeClass)

  override def replaceRoleRestriction[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit =
    restrictions.put(classTag[A].toString, List(classTag[B].runtimeClass))

  override def removeRoleRestriction[A <: AnyRef: ClassTag](): Unit = {
    val _ = restrictions.remove(classTag[A].toString)
  }

  override def validate[R <: AnyRef: ClassTag](player: AnyRef, role: R): Unit =
    val currentRestrictions = restrictions.toList
    if (currentRestrictions.nonEmpty) {
      val roleInterface = classTag[R].runtimeClass.getDeclaredMethods
      if (
        currentRestrictions.exists { case (pt, rts) =>
          ReflectiveHelper.isInstanceOf(pt, player.getClass.toString) && !rts
            .exists(r => ReflectiveHelper.isSameInterface(roleInterface, r.getDeclaredMethods))
        }
      ) {
        throw new RuntimeException(s"Role '$role' can not be played by '$player' due to the active role restrictions!")
      }
    }

}
