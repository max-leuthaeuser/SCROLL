package scroll.internal.support

import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag

private[internal] object RoleGroupsApi {

  sealed trait Constraint

  object AND extends Constraint

  object OR extends Constraint

  object XOR extends Constraint

  object NOT extends Constraint

  type CInt = Ordered[Int]

  sealed trait Entry {
    def types: Seq[String]
  }

  object Types {
    def apply(ts: String*): Types = new Types(ts.map(ReflectiveHelper.simpleName))
  }

  final class Types(ts: Seq[String]) extends Entry {
    override def types: Seq[String] = ts
  }

}

trait RoleGroupsApi {

  import RoleGroupsApi._

  /**
    * Wrapping function that checks all available role group constraints for
    * all core objects and its roles after the given function was executed.
    * Throws a RuntimeException if a role group constraint is violated!
    *
    * @param func the function to execute and check role group constraints afterwards
    */
  def checked(func: => Unit): Unit

  /**
    * Creates a [[scroll.internal.support.RoleGroupsApi.RoleGroupApi]] with the given name
    * with a fluent relationship creation API.
    *
    * @param name the name of the created RoleGroup
    * @return an instance of RoleGroupApi
    */
  def create(name: String): RoleGroupApi

  trait RoleGroupApi extends Entry {

    def containing(rg: RoleGroupApi*)
                  (limitLower: Int, limitUpper: CInt)
                  (occLower: Int, occUpper: CInt): RoleGroupApi

    def containing[T1 <: AnyRef : ClassTag](limitLower: Int, limitUpper: CInt)
                                           (occLower: Int, occUpper: CInt): RoleGroupApi


    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroupApi

    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroupApi

    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag, T4 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroupApi


    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag, T4 <: AnyRef : ClassTag, T5 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroupApi
  }

}
