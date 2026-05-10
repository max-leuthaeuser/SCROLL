package scroll.internal.support.impl

import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution
import org.chocosolver.solver.variables.IntVar
import scroll.internal.errors.SCROLLErrors.DuplicateRoleGroup
import scroll.internal.errors.SCROLLErrors.InvalidRoleGroupConstraint
import scroll.internal.errors.SCROLLErrors.InvalidRoleGroupEntry
import scroll.internal.errors.SCROLLErrors.MissingRoleGroupConstraint
import scroll.internal.errors.SCROLLErrors.RoleGroupInnerCardinalityViolation
import scroll.internal.errors.SCROLLErrors.RoleGroupOccurrenceCardinalityViolation
import scroll.internal.errors.SCROLLErrors.UnsolvableRoleGroupConstraint
import scroll.internal.graph.RoleGraphProxyApi
import scroll.internal.support.RoleGroupsApi
import scroll.internal.util.ReflectiveHelper

import scala.collection.mutable
import scala.collection.concurrent.TrieMap
import scala.reflect.ClassTag
import scala.reflect.classTag

class RoleGroups(private val roleGraph: RoleGraphProxyApi) extends RoleGroupsApi {

  import RoleGroupsApi._

  private lazy val roleGroups = TrieMap.empty[String, RoleGroup]

  override def checked(func: => Unit): Unit = {
    func
    validate(roleGroups.values.toSeq)
  }

  override def create(name: String): RoleGroupApi = RoleGroup(name, Seq.empty, (0, 0), (0, 0))

  private def validateOccurrenceCardinality(groups: Seq[RoleGroup]): Unit =
    groups.foreach { rg =>
      val name   = rg.name
      val min    = rg.occ._1
      val max    = rg.occ._2
      val types  = rg.types
      val actual = types
        .map(ts => roleGraph.plays.allPlayers.count(r => ts == ReflectiveHelper.simpleName(r.getClass.toString)))
        .sum
      if (actual < min || max < actual) {
        throw RoleGroupOccurrenceCardinalityViolation(name, types, actual, min, max.toString)
      }
    }

  private def buildConstraintsMap(
    types: Seq[String],
    op: Constraint,
    model: Model,
    numOfTypes: Int,
    min: Int,
    max: CInt,
    rg: RoleGroup
  ): Map[String, IntVar] =
    types.map { ts =>
      op match {
        case AND => ts -> model.intVar("NUM$" + ts, 1)
        case OR  => ts -> model.intVar("NUM$" + ts, 0, numOfTypes)
        case XOR => ts -> model.intVar("NUM$" + ts, 0, 1)
        case NOT => ts -> model.intVar("NUM$" + ts, 0)
      }
    }.toMap

  private def solve(
    model: Model,
    types: Seq[String],
    constraintsMap: Map[String, IntVar],
    rg: RoleGroup
  ): Seq[String] = {
    val solver = model.getSolver
    if (solver.solve()) {
      val resultRoleTypeSet = mutable.Set.empty[String]

      val solutions = mutable.ListBuffer.empty[Solution]
      while {
        val sol = new Solution(model)
        val _   = sol.record()
        solutions += sol
        solver.solve()
      } do ()

      val allPlayers =
        roleGraph.plays.allPlayers.filter(p => !types.contains(ReflectiveHelper.simpleName(p.getClass.toString)))
      if (
        allPlayers.forall { p =>
          solutions.exists { s =>
            types.forall { t =>
              val numRole = roleGraph.plays
                .roles(p)
                .count(r => t == ReflectiveHelper.simpleName(r.getClass.toString))
              if (numRole == s.getIntVal(constraintsMap.getOrElse(t, throw MissingRoleGroupConstraint(rg.name, t)))) {
                resultRoleTypeSet.add(t)
                true
              } else {
                false
              }
            }
          }
        }
      ) {
        return resultRoleTypeSet.toSeq // scalastyle:ignore
      }

    } else {
      throw UnsolvableRoleGroupConstraint(rg.name)
    }
    // give up
    throw RoleGroupInnerCardinalityViolation(rg.name)
  }

  private def resolveTypes(rg: RoleGroup, evaluatedGroups: mutable.Map[String, Seq[String]]): Seq[String] =
    rg.entries.flatMap {
      case ts: Types         => ts.types
      case nested: RoleGroup => eval(nested, evaluatedGroups)
      case _                 => throw InvalidRoleGroupEntry()
    }

  private def eval(rg: RoleGroup, evaluatedGroups: mutable.Map[String, Seq[String]]): Seq[String] =
    evaluatedGroups.getOrElseUpdate(
      rg.name, {
        val model      = new Model(s"MODEL$$${rg.hashCode()}")
        val types      = resolveTypes(rg, evaluatedGroups)
        val numOfTypes = types.size
        val sumName    = "SUM$" + rg.name

        val (min, max) = (rg.limit._1, rg.limit._2)

        val (sum, op) = (min, max) match {
          case _ if max.compare(min) == 0 && min == numOfTypes =>
            (model.intVar(sumName, numOfTypes), AND)
          case _ if min == 1 && max.compare(numOfTypes) == 0 =>
            (model.intVar(sumName, 1, numOfTypes), OR)
          case _ if min == 1 && max.compare(1) == 0 => (model.intVar(sumName, 1), XOR)
          case _ if min == 0 && max.compare(0) == 0 => (model.intVar(sumName, 0), NOT)
          case _                                    => throw InvalidRoleGroupConstraint(rg.name, min, max.toString)
        }

        val constraintsMap = buildConstraintsMap(types, op, model, numOfTypes, min, max, rg)
        model.post(model.sum(constraintsMap.values.toArray, "=", sum))
        solve(model, types, constraintsMap, rg)
      }
    )

  private def validateInnerCardinality(groups: Seq[RoleGroup]): Unit = {
    val evaluatedGroups = mutable.Map.empty[String, Seq[String]]
    groups.foreach(eval(_, evaluatedGroups))
  }

  /** Checks all role groups.
    *
    * Raises typed role-group validation errors when an occurrence or inner-cardinality constraint is violated.
    */
  private def validate(groups: Seq[RoleGroup]): Unit = {
    validateOccurrenceCardinality(groups)
    validateInnerCardinality(groups)
  }

  private def addRoleGroup(rg: RoleGroup): RoleGroup =
    if (roleGroups.putIfAbsent(rg.name, rg).nonEmpty) {
      throw DuplicateRoleGroup(rg.name)
    } else {
      rg
    }

  case class RoleGroup(name: String, entries: Seq[Entry], limit: (Int, CInt), occ: (Int, CInt)) extends RoleGroupApi {
    assert(occ._1 >= 0 && occ._2 >= occ._1)
    assert(limit._1 >= 0 && limit._2 >= limit._1)

    implicit private def classTagToString(m: ClassTag[?]): String =
      ReflectiveHelper.simpleName(m.toString)

    override def types: Seq[String] =
      resolveTypes(this, mutable.Map.empty[String, Seq[String]])

    override def containing(
      rg: RoleGroupApi*
    )(limitLower: Int, limitUpper: CInt)(occLower: Int, occUpper: CInt): RoleGroupApi =
      addRoleGroup(RoleGroup(name, rg, (limitLower, limitUpper), (occLower, occUpper)))

    override def containing[T1 <: AnyRef: ClassTag](
      limitLower: Int,
      limitUpper: CInt
    )(occLower: Int, occUpper: CInt): RoleGroupApi = {
      val entry = Types(classTag[T1])
      addRoleGroup(RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    override def containing[T1 <: AnyRef: ClassTag, T2 <: AnyRef: ClassTag](
      limitLower: Int,
      limitUpper: CInt
    )(occLower: Int, occUpper: CInt): RoleGroupApi = {
      val entry = Types(classTag[T1], classTag[T2])
      addRoleGroup(RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    override def containing[T1 <: AnyRef: ClassTag, T2 <: AnyRef: ClassTag, T3 <: AnyRef: ClassTag](
      limitLower: Int,
      limitUpper: CInt
    )(occLower: Int, occUpper: CInt): RoleGroupApi = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3])
      addRoleGroup(RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    override def containing[
      T1 <: AnyRef: ClassTag,
      T2 <: AnyRef: ClassTag,
      T3 <: AnyRef: ClassTag,
      T4 <: AnyRef: ClassTag
    ](limitLower: Int, limitUpper: CInt)(occLower: Int, occUpper: CInt): RoleGroupApi = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4])
      addRoleGroup(RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

    override def containing[
      T1 <: AnyRef: ClassTag,
      T2 <: AnyRef: ClassTag,
      T3 <: AnyRef: ClassTag,
      T4 <: AnyRef: ClassTag,
      T5 <: AnyRef: ClassTag
    ](limitLower: Int, limitUpper: CInt)(occLower: Int, occUpper: CInt): RoleGroupApi = {
      val entry = Types(classTag[T1], classTag[T2], classTag[T3], classTag[T4], classTag[T5])
      addRoleGroup(RoleGroup(name, Seq(entry), (limitLower, limitUpper), (occLower, occUpper)))
    }

  }

}
