package scroll.internal.support

import scala.reflect.ClassTag

/** Allows to add and check role restrictions (in the sense of structural typing) to a compartment instance.
  */
trait RoleRestrictionsApi {

  /** Add a role restriction between the given player type A and role type B.
    *
    * @tparam A
    *   the player type
    * @tparam B
    *   the role type
    */
  def addRoleRestriction[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit

  /** Replaces a role restriction for a player of type A with a new role restriction between the given player type A and
    * role type B.
    *
    * @tparam A
    *   the player type
    * @tparam B
    *   the role type
    */
  def replaceRoleRestriction[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit

  /** Removes all role restriction for a player of type A.
    *
    * @tparam A
    *   the player type
    */
  def removeRoleRestriction[A <: AnyRef: ClassTag](): Unit

  /** Checks all role restriction between the given player and a role type. Will throw a RuntimeException if a
    * restriction is violated!
    *
    * @param player
    *   the player instance to check
    * @param role
    *   the role type to check
    */
  def validate[R <: AnyRef: ClassTag](player: AnyRef, role: R): Unit
}
