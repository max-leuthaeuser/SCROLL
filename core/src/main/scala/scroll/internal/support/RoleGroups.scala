package scroll.internal.support

import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar
import scroll.internal.ICompartment
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.classTag

trait RoleGroups {
  self: ICompartment =>

  private[this] lazy val roleGroups = mutable.HashMap.empty[String, RoleGroup]

  private[this] sealed trait Constraint

  private[this] object AND extends Constraint

  private[this] object OR extends Constraint

  private[this] object XOR extends Constraint

  private[this] object NOT extends Constraint

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

  private[this] def validateOccurrenceCardinality(): Unit = {
    roleGroups.foreach { case (name, rg) =>
      val min = rg.occ._1
      val max = rg.occ._2
      val types = rg.types
      val actual = types.map(ts => plays.allPlayers.count(r => ts == ReflectiveHelper.classSimpleClassName(r.getClass.toString))).sum
      if (actual < min || max < actual) {
        throw new RuntimeException(s"Occurrence cardinality in role group '$name' violated! " +
          s"Roles '$types' are played $actual times but should be between $min and $max.")
      }
    }
  }

  private[this] def buildConstraintsMap(types: Seq[String], op: Constraint, model: Model, numOfTypes: Int, min: Int, max: CInt, rg: RoleGroup): Map[String, IntVar] = types.map(ts => op match {
    case AND => ts -> model.intVar("NUM$" + ts, 1)
    case OR => ts -> model.intVar("NUM$" + ts, 0, numOfTypes)
    case XOR => ts -> model.intVar("NUM$" + ts, 0, 1)
    case NOT => ts -> model.intVar("NUM$" + ts, 0)
    case _ =>
      throw new RuntimeException(s"Role group constraint of ($min, $max) for role group '${rg.name}' not possible!")
  }).toMap

  private[this] def solve(model: Model, types: Seq[String], constraintsMap: Map[String, IntVar], rg: RoleGroup): Seq[String] = {
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
            val numRole = plays.roles(p).count(r => t == ReflectiveHelper.classSimpleClassName(r.getClass.toString))
            if (numRole == s.getIntVal(constraintsMap(t))) {
              resultRoleTypeSet.add(t)
              true
            } else {
              false
            }
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

  private[this] def eval(rg: RoleGroup): Seq[String] = {
    val model = new Model("MODEL$" + rg.hashCode())
    val types = rg.types
    val numOfTypes = types.size
    val sumName = "SUM$" + rg.name

    val (min, max) = (rg.limit._1, rg.limit._2)

    val (sum, op) = (min, max) match {
      case _ if max.compare(min) == 0 && min == numOfTypes => (model.intVar(sumName, numOfTypes), AND)
      case _ if min == 1 && max.compare(numOfTypes) == 0 => (model.intVar(sumName, 1, numOfTypes), OR)
      case _ if min == 1 && max.compare(1) == 0 => (model.intVar(sumName, 1), XOR)
      case _ if min == 0 && max.compare(0) == 0 => (model.intVar(sumName, 0), NOT)
      case _ => throw new RuntimeException(s"Role group constraint of ($min, $max) for role group '${rg.name}' not possible!")
    }

    val constraintsMap = buildConstraintsMap(types, op, model, numOfTypes, min, max, rg)
    model.post(model.sum(constraintsMap.values.toArray, "=", sum))
    solve(model, types, constraintsMap, rg)
  }

  private[this] def validateInnerCardinality(): Unit = {
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
  private[this] def validate(): Unit = {
    validateOccurrenceCardinality()
    validateInnerCardinality()
  }

  private[this] def addRoleGroup(rg: RoleGroup): RoleGroup = {
    if (roleGroups.exists { case (n, _) => n == rg.name }) {
      throw new RuntimeException(s"The RoleGroup ${rg.name} was already added!")
    } else {
      roleGroups(rg.name) = rg
      rg
    }
  }

  private[this] type CInt = Ordered[Int]

  trait Entry {
    def types: Seq[String]
  }

  object Types {
    def apply(ts: String*): Types = new Types(ts.map(ReflectiveHelper.typeSimpleClassName))
  }

  class Types(ts: Seq[String]) extends Entry {
    def types: Seq[String] = ts
  }

  case class RoleGroup(name: String, entries: Seq[Entry], limit: (Int, CInt), occ: (Int, CInt), var evaluated: Boolean = false) extends Entry {
    assert(0 <= occ._1 && occ._2 >= occ._1)
    assert(0 <= limit._1 && limit._2 >= limit._1)

    private[this] implicit def classTagToString(m: ClassTag[_]): String = ReflectiveHelper.simpleName(m.toString)

    def types: Seq[String] = entries.flatMap {
      case ts: Types => ts.types
      case rg: RoleGroup => eval(rg)
      case _ => throw new RuntimeException("Role groups can only contain a list of types or role groups itself!")
    }

    def containing(rg: RoleGroup*)
                  (limitLower: Int, limitUpper: CInt)
                  (occLower: Int, occUpper: CInt): RoleGroup =
      addRoleGroup(new RoleGroup(name, rg, (limitLower, limitUpper), (occLower, occUpper)))

    def containing[T1 <: AnyRef : ClassTag](limitLower: Int, limitUpper: CInt)
                                           (occLower: Int, occUpper: CInt): RoleGroup = {
      val entry = Types(classTag[T1])
      addRoleGroup(new RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }


    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroup = {
      val entry = Types(classTag[T1], classTag[T2])
      addRoleGroup(new RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroup = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3])
      addRoleGroup(new RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag, T4 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroup = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4])
      addRoleGroup(new RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }


    def containing[T1 <: AnyRef : ClassTag, T2 <: AnyRef : ClassTag, T3 <: AnyRef : ClassTag, T4 <: AnyRef : ClassTag, T5 <: AnyRef : ClassTag]
    (limitLower: Int, limitUpper: CInt)
    (occLower: Int, occUpper: CInt): RoleGroup = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4], classTag[T5])
      addRoleGroup(new RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }
  }

  object RoleGroup {
    def apply(name: String): RoleGroup = new RoleGroup(name, Seq.empty, (0, 0), (0, 0))
  }

}
