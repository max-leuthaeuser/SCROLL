package scroll.internal.support

import org.chocosolver.solver.constraints.IntConstraintFactory

import scala.collection.JavaConversions._

import org.chocosolver.solver.Solver
import org.chocosolver.solver.search.strategy.IntStrategyFactory
import org.chocosolver.solver.variables.{IntVar, VariableFactory}
import scroll.internal.Compartment
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable

trait RoleGroups {
  self: Compartment =>

  private lazy val roleGroups = mutable.HashMap.empty[String, RoleGroup]

  private sealed trait Constraint

  private case class AND() extends Constraint

  private case class OR() extends Constraint

  private case class XOR() extends Constraint

  private case class NOT() extends Constraint

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

  private def validateOccurrenceCardinality() {
    roleGroups.foreach { case (name, rg) =>
      rg.getTypes.foreach(ts => {
        val min = rg.occ._1
        val max = rg.occ._2
        val actual = plays.allPlayers.count(r => ts == ReflectiveHelper.classSimpleClassName(r.getClass.toString))
        if (actual < min || max <= actual)
          throw new RuntimeException(s"Occurrence cardinality in role group '$name' violated! " +
            s"Role '$ts' is played $actual times but should be between $min and $max.")
      })
    }
  }

  private def eval(rg: RoleGroup): Seq[String] = {
    val solver = new Solver("SOLVER$" + rg.hashCode())
    val types = rg.getTypes
    val numOfTypes = types.size
    val min = rg.limit._1
    val max = rg.limit._2

    val sumName = "SUM$" + rg.name
    var sum: IntVar = null
    var op: Constraint = null

    // AND
    if (max.compare(min) == 0 && min == numOfTypes) {
      sum = VariableFactory.fixed(sumName, numOfTypes, solver)
      op = AND()
    }

    // OR
    if (min == 1 && max.compare(numOfTypes) == 0) {
      sum = VariableFactory.bounded(sumName, 1, numOfTypes, solver)
      op = OR()
    }

    // XOR
    if (min == 1 && max.compare(1) == 0) {
      sum = VariableFactory.fixed(sumName, 1, solver)
      op = XOR()
    }

    // NOT
    if (min == 0 && max.compare(0) == 0) {
      sum = VariableFactory.fixed(sumName, 0, solver)
      op = NOT()
    }

    val constrMap = types.map(ts => op match {
      case AND() => ts -> VariableFactory.fixed("NUM$" + ts, 1, solver)
      case OR() => ts -> VariableFactory.bounded("NUM$" + ts, 0, numOfTypes, solver)
      case XOR() => ts -> VariableFactory.bounded("NUM$" + ts, 0, 1, solver)
      case NOT() => ts -> VariableFactory.fixed("NUM$" + ts, 0, solver)
    }).toMap

    solver.post(IntConstraintFactory.sum(constrMap.values.toArray, sum))

    solver.set(IntStrategyFactory.lexico_LB(constrMap.values.toArray: _*))
    if (!solver.findSolution()) {
      throw new RuntimeException(s"Constraint set of role group '${rg.name}' unsolvable!")
    }

    val resultRoleTypeSet = mutable.HashSet.empty[String]
    solver.getSolutionRecorder.getSolutions.foreach(s => {
      // TODO: revise
      // println(s.toString)
      if (types.forall(t => {
        plays.allPlayers.find(r => t == ReflectiveHelper.classSimpleClassName(r.getClass.toString)) match {
          case Some(role) =>
            val player = +role player
            val numRole = plays.getRoles(player).count(r => t == ReflectiveHelper.classSimpleClassName(r.getClass.toString))
            resultRoleTypeSet += t
            numRole == s.getIntVal(constrMap(t))
          case None if op == NOT() => true
          case None => throw new RuntimeException(s"Role instance of type '$t' was never played, but is addressed in role group '${rg.name}'!")
        }
      })) {
        rg.evaluated = true
        return resultRoleTypeSet.toSeq
      }
    })

    // give up
    throw new RuntimeException(s"Constraint set for inner cardinality of role group '${rg.name}' violated!")

  }

  private def validateInnerCardinality() {
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
  private def validate() {
    validateOccurrenceCardinality()
    validateInnerCardinality()
  }

  private def addRoleGroup(rg: RoleGroup): RoleGroup = {
    roleGroups.exists { case (n, _) => n == rg.name } match {
      case true => throw new RuntimeException(s"The RoleGroup ${rg.name} was already added!")
      case false => roleGroups(rg.name) = rg; rg
    }
  }

  private type CInt = Ordered[Int]

  trait Entry {
    def getTypes: Seq[String]
  }

  object Types {
    def apply(ts: String*): Types = new Types(ts.map(ReflectiveHelper.classSimpleClassName))
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
      case _ => throw new RuntimeException("Rolegroups can only contain a list of types or Rolegroups itself!")
    }
  }

  object RoleGroup {
    private implicit def manifest2String(m: Manifest[_]): String = m.toString()

    def apply(name: String) = new {

      def containing(rg: RoleGroup*)(limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt) =
        addRoleGroup(new RoleGroup(name, rg, (limit_l, limit_u), (occ_l, occ_u)))

      def containing[T1: Manifest](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt) = {
        val entry = Types(manifest[T1])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }


      def containing[T1: Manifest, T2: Manifest](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

      def containing[T1: Manifest, T2: Manifest, T3: Manifest](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

      def containing[T1: Manifest, T2: Manifest, T3: Manifest, T4: Manifest](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3], manifest[T4])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }


      def containing[T1: Manifest, T2: Manifest, T3: Manifest, T4: Manifest, T5: Manifest](limit_l: Int, limit_u: CInt)(occ_l: Int, occ_u: CInt): RoleGroup = {
        val entry = Types(manifest[T1], manifest[T2], manifest[T3], manifest[T4], manifest[T5])
        addRoleGroup(new RoleGroup(name, Seq(entry), (limit_l, limit_u), (occ_l, occ_u)))
      }

    }
  }

}
