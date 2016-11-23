package scroll.internal.support

import org.chocosolver.solver.{Model, Solution}
import org.chocosolver.solver.variables.IntVar
import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.reflect.{ClassTag, classTag}
import scala.collection.mutable

trait RoleGroups {
  self: Compartment =>

  private lazy val roleGroups = mutable.HashMap.empty[String, RoleGroup]

  private sealed trait Constraint

  private object AND extends Constraint

  private object OR extends Constraint

  private object XOR extends Constraint

  private object NOT extends Constraint

  /**
    * Wrapping function that checks all available role group constraints for
    * all core objects and its roles after the given function was executed.
    * Throws a RuntimeException if a role group constraint is violated!
    *
    * @param func the function to execute and check role group constraints afterwards
    */
  def RoleGroupsChecked(func: => Unit): Unit = {
    func
    validate()
  }

  private def validateOccurrenceCardinality(): Unit = {
    roleGroups.foreach { case (name, rg) =>
      val min = rg.occ._1
      val max = rg.occ._2
      val types = rg.getTypes
      val actual = types.map(ts => plays.allPlayers.count(r => ts == ReflectiveHelper.classSimpleClassName(r.getClass.toString))).sum
      if (actual < min || max < actual) {
        throw new RuntimeException(s"Occurrence cardinality in role group '$name' violated! " +
          s"Roles '$types' are played $actual times but should be between $min and $max.")
      }
    }
  }

  private def eval(rg: RoleGroup): Seq[String] = {
    val model = new Model("MODEL$" + rg.hashCode())
    val types = rg.getTypes
    val numOfTypes = types.size
    val min = rg.limit._1
    val max = rg.limit._2

    val sumName = "SUM$" + rg.name
    var sum = Option.empty[IntVar]
    var op = Option.empty[Constraint]

    // AND
    if (max.compare(min) == 0 && min == numOfTypes) {
      sum = Some(model.intVar(sumName, numOfTypes))
      op = Some(AND)
    }

    // OR
    if (min == 1 && max.compare(numOfTypes) == 0) {
      sum = Some(model.intVar(sumName, 1, numOfTypes))
      op = Some(OR)
    }

    // XOR
    if (min == 1 && max.compare(1) == 0) {
      sum = Some(model.intVar(sumName, 1))
      op = Some(XOR)
    }

    // NOT
    if (min == 0 && max.compare(0) == 0) {
      sum = Some(model.intVar(sumName, 0))
      op = Some(NOT)
    }

    val constrMap = types.map(ts => op match {
      case Some(AND) => ts -> model.intVar("NUM$" + ts, 1)
      case Some(OR) => ts -> model.intVar("NUM$" + ts, 0, numOfTypes)
      case Some(XOR) => ts -> model.intVar("NUM$" + ts, 0, 1)
      case Some(NOT) => ts -> model.intVar("NUM$" + ts, 0)
      case None => throw new RuntimeException(s"Role group constraint of ($min, $max) for role group '${rg.name}' not possible!")
    }).toMap

    sum match {
      case Some(s) =>
        model.post(model.sum(constrMap.values.toArray, "=", s))
      case None => throw new RuntimeException(s"Role group constraint of ($min, $max) for role group '${rg.name}' not possible!")
    }

    val solver = model.getSolver
    if (solver.solve()) {
      val resultRoleTypeSet = mutable.Set.empty[String]

      val solutions = mutable.ListBuffer.empty[Solution]
      do {
        val sol = new Solution(model)
        sol.record()
        solutions += sol
      } while (solver.solve())

      val allPlayers = plays.allPlayers.filter(p => !types.contains(ReflectiveHelper.classSimpleClassName(p.getClass.toString)))
      if (allPlayers.forall(p => {
        solutions.exists(s => {
          types.forall(t => {
            val numRole = plays.getRoles(p).count(r => t == ReflectiveHelper.classSimpleClassName(r.getClass.toString))
            if (numRole == s.getIntVal(constrMap(t))) {
              resultRoleTypeSet.add(t)
              true
            } else false
          })
        })
      })) {
        rg.evaluated = true
        return resultRoleTypeSet.toSeq
      }

    } else {
      throw new RuntimeException(s"Constraint set of role group '${rg.name}' unsolvable!")
    }
    // give up
    throw new RuntimeException(s"Constraint set for inner cardinality of role group '${rg.name}' violated!")
  }

  private def validateInnerCardinality(): Unit = {
    try {
      roleGroups.values.filter(!_.evaluated).foreach(eval)
    } finally {
      roleGroups.values.foreach(_.evaluated = false)
    }
  }

  /**
    * Checks all role groups.
    * Will throw a RuntimeException if a role group constraint is violated!
    */
  private def validate(): Unit = {
    validateOccurrenceCardinality()
    validateInnerCardinality()
  }

  private def addRoleGroup(rg: RoleGroup): RoleGroup = {
    if (roleGroups.exists { case (n, _) => n == rg.name }) {
      throw new RuntimeException(s"The RoleGroup ${rg.name} was already added!")
    } else {
      roleGroups(rg.name) = rg
      rg
    }
  }

  private type CInt = Ordered[Int]

  trait Entry {
    def getTypes: Seq[String]
  }

  object Types {
    def apply(ts: String*): Types = new Types(ts.map(ReflectiveHelper.typeSimpleClassName))
  }

  class Types(ts: Seq[String]) extends Entry {
    def getTypes: Seq[String] = ts
  }

  case class RoleGroup(name: String, entries: Seq[Entry], limit: (Int, CInt), occ: (Int, CInt), var evaluated: Boolean = false) extends Entry {
    assert(0 <= occ._1 && occ._2 >= occ._1)
    assert(0 <= limit._1 && limit._2 >= limit._1)

    def getTypes: Seq[String] = entries.flatMap {
      case ts: Types => ts.getTypes
      case rg: RoleGroup => eval(rg)
      case _ => throw new RuntimeException("Role groups can only contain a list of types or role groups itself!")
    }
  }

  object RoleGroup {
    private implicit def classTagToString(m: ClassTag[_]): String = ReflectiveHelper.simpleName(m.toString)

    def apply(name: String) = new {

      def containing(rg: RoleGroup*)(limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup =
        addRoleGroup(new RoleGroup(name, rg, (limit_l, limit_u), (occ_l, occ_u)))

      def containing[T1: ClassTag](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(classTag[T1])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }


      def containing[T1: ClassTag, T2: ClassTag](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(classTag[T1], classTag[T2])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

      def containing[T1: ClassTag, T2: ClassTag, T3: ClassTag](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(classTag[T1], classTag[T2], classTag[T3])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

      def containing[T1: ClassTag, T2: ClassTag, T3: ClassTag, T4: ClassTag](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }


      def containing[T1: ClassTag, T2: ClassTag, T3: ClassTag, T4: ClassTag, T5: ClassTag](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4], classTag[T5])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

    }
  }

}
