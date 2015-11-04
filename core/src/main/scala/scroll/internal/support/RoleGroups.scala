package scroll.internal.support

import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable

trait RoleGroups {
  self: Compartment =>

  private lazy val roleGroups = mutable.HashMap.empty[String, RoleGroup]

  /**
    * Wrapping function that checks all available role group constraints for
    * all core objects and its roles after the given function was executed.
    * Throws a RuntimeException if a role group constraint is violated!
    *
    * @param func the function to execute and check role group constraints afterwards
    */
  def RoleGroupsChecked(func: => Unit) {
    func
    validate()
  }

  /**
    * Checks all role groups.
    * Will throw a RuntimeException if a role group constraint is violated!
    */
  private def validate() {
    ???
  }

  private def addRoleGroup(rg: RoleGroup): RoleGroup = {
    roleGroups.exists { case (n, _) => n == rg.name } match {
      case true => throw new RuntimeException(s"The RoleGroup ${rg.name} was already added!")
      case false => roleGroups(rg.name) = rg; rg
    }
  }

  trait Entry {
    def getTypes: Seq[String]
  }

  object Types {
    def apply(ts: String*): Types = new Types(ts.map(ReflectiveHelper.classSimpleClassName))
  }

  class Types(ts: Seq[String]) extends Entry {
    def getTypes: Seq[String] = ts
  }

  case class RoleGroup(name: String, entries: Seq[Entry], occ: (Int, Int), limit: (Int, Int)) extends Entry {
    assert(0 <= occ._1 && occ._1 <= occ._2)
    assert(0 <= limit._1 && limit._1 <= limit._2)

    def getTypes: Seq[String] = entries.flatMap {
      case ts: Types => ts.getTypes
      case RoleGroup(_, e, _, _) => e.flatMap(_.getTypes)
      case _ => throw new RuntimeException("Rolegroups can only contain a list of types or Rolegroups itself!")
    }
  }

  object RoleGroup {
    private implicit def manifest2String(m: Manifest[_]): String = m.toString()

    def apply(name: String) = new {

      def containing(rg: RoleGroup*)(occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int) =
        addRoleGroup(new RoleGroup(name, rg, (occ_l, occ_u), (limit_l, limit_u)))

      def containing[T1: Manifest](occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int) = {
        val entry = Types(manifest[T1])
        addRoleGroup(new RoleGroup(name, Seq(entry), (occ_l, occ_u), (limit_l, limit_u)))
      }


      def containing[T1: Manifest, T2: Manifest](occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2])
        addRoleGroup(new RoleGroup(name, Seq(entry), (occ_l, occ_u), (limit_l, limit_u)))
      }

      def containing[T1: Manifest, T2: Manifest, T3: Manifest](occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3])
        addRoleGroup(new RoleGroup(name, Seq(entry), (occ_l, occ_u), (limit_l, limit_u)))
      }

      def containing[T1: Manifest, T2: Manifest, T3: Manifest, T4: Manifest](occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3], manifest[T4])
        addRoleGroup(new RoleGroup(name, Seq(entry), (occ_l, occ_u), (limit_l, limit_u)))
      }


      def containing[T1: Manifest, T2: Manifest, T3: Manifest, T4: Manifest, T5: Manifest](occ_l: Int, occ_u: Int)(limit_l: Int, limit_u: Int): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3], manifest[T4], manifest[T5])
        addRoleGroup(new RoleGroup(name, Seq(entry), (occ_l, occ_u), (limit_l, limit_u)))
      }

    }
  }

}
