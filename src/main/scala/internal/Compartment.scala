package internal

import scala.collection.mutable
import scala.collection.immutable.Queue
import java.lang.reflect.Method
import java.lang
import internal.dispatch._
import util.QueueUtils._
import scala.Some

// TODO: what happens if the same role is played multiple times from one player?
trait Compartment
{
  implicit def anyToRole[T](any: T): RoleType[T] = new RoleType[T](any)

  implicit def anyToPlayer[T](any: T): PlayerType[T] = new PlayerType[T](any)

  val plays = new mutable.HashMap[Any, mutable.Set[Any]]() with mutable.MultiMap[Any, Any]
  {
    override def default
    (key: Any) = mutable.Set.empty
  }

  // declaring a is-part-of relation between compartments
  def >+>(other: Compartment)
  {
    other.plays.foreach(v => {
      val key = v._1
      val values = v._2
      values.foreach(plays.addBinding(key, _))
    })
  }

  // removing is-part-of relation between compartments
  def <-<(other: Compartment)
  {
    other.plays.foreach(v => {
      val key = v._1
      val values = v._2
      values.foreach(plays.removeBinding(key, _))
    })
  }

  def E_?[T](any: T): T =
    (plays.keys.toList ::: plays.values.flatten.toList)
      .find(v => any.getClass.getSimpleName equals v.getClass.getSimpleName) match {
      case Some(value) => value.asInstanceOf[T]
      case None => throw new RuntimeException(s"No player with type '$any' found.")
    }

  def A_?[T](any: T): Seq[T] =
    (plays.keys.toList ::: plays.values.flatten.toList)
      .filter(v => any.getClass.getSimpleName equals v.getClass.getSimpleName).map(_.asInstanceOf[T])

  def addPlaysRelation(
    core: Any,
    role: Any
    )
  {
    plays.addBinding(core, role)
  }

  def removePlaysRelation(
    core: Any,
    role: Any
    )
  {
    plays.removeBinding(core, role)
  }

  def transferRole(
    coreFrom: Any,
    coreTo: Any,
    role: Any
    )
  {
    assert(coreFrom != coreTo)
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  def transferRoles(
    coreFrom: Any,
    coreTo: Any,
    roles: Set[Any]
    )
  {
    roles.foreach(transferRole(coreFrom, coreTo, _))
  }

  def getRelation(core: Any): mutable.Set[Any] = plays(core)

  def getOtherRolesForRole(role: Any): mutable.Set[Any] =
  {
    plays.values.foreach(set => {
      if (set.exists(_ == role)) return set
    })
    mutable.Set[Any]()
  }

  def getCoreFor(role: Any): Any = role match {
    case cur: RoleType[_] => getCoreFor(cur.role)
    case cur: PlayerType[_] => getCoreFor(cur.core)
    // default:
    case cur: Any => plays.foreach {
      case (
        k,
        v) => if (v.contains(cur)) return k
    }
  }

  trait DispatchType
  {
    // for single method dispatch
    def dispatch[E](
      on: Any,
      m: Method
      ): E = m.invoke(on).asInstanceOf[E]

    // for multi-method / multi-argument dispatch
    def dispatch[E, A](
      on: Any,
      m: Method,
      args: Seq[A]
      ): E =
    {
      val argTypes: Array[Class[_]] = m.getParameterTypes
      val actualArgs: Seq[Any] = args.zip(argTypes).map {
        case (arg: PlayerType[_], tpe: Class[_]) =>
          getRelation(arg.core).find(_.getClass == tpe) match {
            case Some(curRole) => curRole
            // TODO: how to permit this?
            case None => throw new RuntimeException(s"No role for type '$tpe' found.")
          }
        case (arg: Double, tpe: Class[_]) => new lang.Double(arg.toDouble)
        // TODO: warning: abstract type A gets eliminated by erasure
        case (arg: A, tpe: Class[_]) => tpe.cast(arg)
      }
      // that looks funny:
      m.invoke(on, actualArgs.map {
        _.asInstanceOf[Object]
      }: _*).asInstanceOf[E]
    }

    // TODO: test this hilarious thing
    def applyDispatchDescription(
      q: Queue[Any],
      dd: DispatchDescription
      ): Queue[Any] =
    {
      def getByName(
        col: Iterable[Any],
        name: String
        ): Option[Any] = col.find(p => p.getClass.getSimpleName equals name)

      def getRolesForCoreByName(
        core: Any,
        name: String
        ): Seq[Any] = getByName(plays(core), name) match {
        case Some(obj) => plays(core).toSeq
        case None => Seq.empty
      }

      dd match {
        case null => q
        case _ =>
          var q_copy = copy(q)
          val player = plays.keys.toList ::: plays.values.flatten.toList

          dd.rules.foreach(rule => {
            getByName(player, rule.in) match {
              case Some(obj) =>
                if ((getRolesForCoreByName(obj, rule.role).toList ::: getOtherRolesForRole(
                  getByName(player, rule.role)).toList).nonEmpty) {
                  rule.precs.foreach {
                    // TODO: bug here
                    case r: Before => q_copy = swapWithOrder(q_copy, (getByName(player, r.leftObj).get, getByName(player, r.rightObj).get))
                    case r: Replace =>
                      q_copy = remove(getByName(player, r.rightObj).get, q_copy)
                    case r: After => q_copy = swapWithOrder(q_copy, (getByName(player, r.rightObj).get,
                      getByName(player, r.leftObj).get))
                  }
                }
              case None => // do nothing
            }
          })
          q_copy
      }
    }
  }

  class RoleType[T](val role: T) extends Dynamic with DispatchType
  {
    def unary_- : RoleType[T] = this

    def applyDynamic[E, A](name: String)
      (args: A*)
      (implicit dd: DispatchDescription = DispatchDescription.empty): E =
    {
      val core = getCoreFor(role)
      val anys = applyDispatchDescription(Queue(core) ++ getOtherRolesForRole(core) :+ role, dd)

      anys.foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })

      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found! (role: '$role')")
    }

    // identity of roles: they don't have their own ID
    override def equals(o: Any) = o match {
      case that: RoleType[_] => that.role == this.role
      case that: PlayerType[_] => that.core == getCoreFor(this)
      case that: Any => that == getCoreFor(this)
    }

    override def hashCode(): Int = role.hashCode()
  }

  class PlayerType[T](val core: T) extends Dynamic with DispatchType
  {
    def play(role: Any): PlayerType[T] =
    {
      core match {
        case p: PlayerType[_] => addPlaysRelation(p.core, role)
        case p: Any => addPlaysRelation(p, role)
      }
      this
    }

    def drop(role: Any): PlayerType[T] =
    {
      removePlaysRelation(core, role)
      this
    }

    def unary_+ : PlayerType[T] = this

    def transfer(role: Any) = new
      {
        def to(player: Any)
        {
          transferRole(this, player, role)
        }
      }

    def applyDynamic[E, A](name: String)
      (args: A*)
      (implicit dd: DispatchDescription = DispatchDescription.empty): E =
    {
      val anys = applyDispatchDescription(
        Queue() ++ getRelation(core) ++ getOtherRolesForRole(core) :+ getCoreFor(core) :+ core, dd)

      anys.foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })
      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found! (core: '$core')")
    }

    override def equals(o: Any) = o match {
      case that: PlayerType[_] => that.core == this.core
      case that: Any => that == this.core
    }

    override def hashCode(): Int = core.hashCode()
  }

}
