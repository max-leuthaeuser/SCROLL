package compartment

import scala.collection.mutable
import java.lang.reflect.Method

// TODO: refactor package structure
trait Compartment
{
  implicit def anyToRole[T](any: T) = new Role[T](any)

  implicit def anyToPlayer[T](any: T) = new Player[T](any)

  val plays = new mutable.HashMap[Any, mutable.Set[Any]]() with mutable.MultiMap[Any, Any]
  {
    override def default
    (key: Any) = mutable.Set.empty
  }

  def addPlaysRelation(
    core: Any,
    role: Any)
  {
    plays.addBinding(core, role)
  }

  def removePlaysRelation(
    core: Any,
    role: Any)
  {
    plays.removeBinding(core, role)
  }

  def transferRole(
    coreFrom: Any,
    coreTo: Any,
    role: Any)
  {
    assert(coreFrom != coreTo)
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  def transferRoles(
    coreFrom: Any,
    coreTo: Any,
    roles: Set[Any])
  {
    roles.foreach(transferRole(coreFrom, coreTo, _))
  }

  def getRelation(core: Any): mutable.Set[Any] = plays(core)

  def getCoreFor(role: Any): Any = role match {
    case cur: Role[_] => plays.foreach {
      case (
        k,
        v) => if (v.contains(cur.role)) return k
    }
    case cur: Player[_] => return getCoreFor(cur.core)
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
      m: Method): E = m.invoke(on).asInstanceOf[E]

    // for multi-method / multi-argument dispatch
    def dispatch[E, A](
      on: Any,
      m: Method,
      args: A*): E =
    {
      val argTypes: Array[Class[_]] = m.getParameterTypes
      val actualArgs: Seq[Any] = args.zip(argTypes).map {
        case (arg: Player[_], tpe: Class[_]) => {
          getRelation(arg.core).find(_.getClass == tpe) match {
            case Some(curRole) => curRole
            // TODO: how to permit this?
            case None => throw new RuntimeException
          }
        }
        // TODO: warning: abstract type A gets eliminated by erasure
        case (arg: A, tpe: Class[_]) => tpe.cast(arg)
      }
      // that looks funny:
      return m.invoke(on, actualArgs.map {
        _.asInstanceOf[Object]
      }: _*).asInstanceOf[E]
    }
  }

  class Role[T](val role: T) extends Dynamic with DispatchType
  {
    def unary_! : Role[T] = this

    def applyDynamic[E, A](name: String)
      (args: A*): E =
    {
      val core = getCoreFor(role)
      core.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        args match {
          case Nil => return dispatch(core, fm)
          case _ => return dispatch(core, fm, args: _*)
        }
      })
      throw new RuntimeException(s"No role with method '$name' found!")
    }

    // TODO: identity of role objects, do they have their owen ID or not?
    // solution here: they don't
    override def equals(o: Any) = o match {
      case that: Role[_] => that.role == this.role
      case that: Any => getCoreFor(this) == that
    }

    override def hashCode(): Int = role.hashCode()
  }

  class Player[T](val core: T) extends Dynamic with DispatchType
  {
    def play(role: Any): Player[T] =
    {
      core match {
        case p: Player[_] => addPlaysRelation(p.core, role)
        case p: Any => addPlaysRelation(p, role)
      }
      this
    }

    def drop(role: Any): Player[T] =
    {
      removePlaysRelation(core, role)
      this
    }

    def unary_~ : Player[T] = this

    def transfer(role: Any) = new
      {
        def to(player: Any)
        {
          transferRole(this, player, role)
        }
      }

    def applyDynamic[E, A](name: String)
      (args: A*): E =
    {
      // search all roles for the given method with name 'name'
      getRelation(core).foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args: _*)
          }
        })
      })
      throw new RuntimeException(s"No role with method '$name' found!")
    }

    override def equals(o: Any) = o match {
      case that: Player[_] => that.core == this.core
      case that: Any => that == this.core
    }

    override def hashCode(): Int = core.hashCode()
  }

}
