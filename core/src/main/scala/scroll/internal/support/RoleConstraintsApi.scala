package scroll.internal.support

import scala.reflect.ClassTag

/** Allows to add and check role constraints (Riehle constraints) to a compartment instance.
  */
trait RoleConstraintsApi {

  /** Adds an role implication constraint between the given role types. Interpretation: if a core
    * object plays an instance of role type A it also has to play an instance of role type B.
    *
    * @tparam A
    *   type of role A
    * @tparam B
    *   type of role B that should be played implicitly if A is played
    */
  def addRoleImplication[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit

  /** Adds an role equivalent constraint between the given role types. Interpretation: if a core
    * object plays an instance of role type A it also has to play an instance of role type B and
    * visa versa.
    *
    * @tparam A
    *   type of role A that should be played implicitly if B is played
    * @tparam B
    *   type of role B that should be played implicitly if A is played
    */
  def addRoleEquivalence[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit

  /** Adds an role prohibition constraint between the given role types. Interpretation: if a core
    * object plays an instance of role type A it is not allowed to play B as well.
    *
    * @tparam A
    *   type of role A
    * @tparam B
    *   type of role B that is not allowed to be played if A is played already
    */
  def addRoleProhibition[A <: AnyRef: ClassTag, B <: AnyRef: ClassTag](): Unit

  /** Wrapping function that checks all available role constraints for all core objects and its
    * roles after the given function was executed. Throws a RuntimeException if a role constraint is
    * violated!
    *
    * @param func
    *   the function to execute and check role constraints afterwards
    */
  def checked(func: => Unit): Unit

}
