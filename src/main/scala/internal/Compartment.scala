package internal

import scala.collection.mutable
import java.lang.reflect.Method
import java.lang

trait Compartment {
  implicit def anyToRole[T](any: T) = new RoleType[T](any)

  implicit def anyToPlayer[T](any: T) = new PlayerType[T](any)

  val plays = new mutable.HashMap[Any, mutable.Set[Any]]() with mutable.MultiMap[Any, Any] {
    override def default
    (key: Any) = mutable.Set.empty
  }

  // declaring a is-part-of relation between compartments
  def >:>(other: Compartment) {
    other.plays.foreach(v => {
      val key = v._1
      val values = v._2
      values.foreach(plays.addBinding(key, _))
    })
  }

  def E_?[T](any: T): T =
    (plays.keys.toList ::: plays.values.flatten.toList).find(v => any.getClass.getSimpleName equals v.getClass.getSimpleName) match {
      case Some(value) => value.asInstanceOf[T]
      case None => throw new RuntimeException("No player with type '" + any.getClass + "' found.")
    }

  def A_?[T](any: T): Seq[T] =
    (plays.keys.toList ::: plays.values.flatten.toList).filter(v => any.getClass.getSimpleName equals v.getClass.getSimpleName).map(_.asInstanceOf[T])

  def addPlaysRelation(core: Any, role: Any) {
    plays.addBinding(core, role)
  }

  def removePlaysRelation(core: Any, role: Any) {
    plays.removeBinding(core, role)
  }

  def transferRole(coreFrom: Any, coreTo: Any, role: Any) {
    assert(coreFrom != coreTo)
    removePlaysRelation(coreFrom, role)
    addPlaysRelation(coreTo, role)
  }

  def transferRoles(coreFrom: Any, coreTo: Any, roles: Set[Any]) {
    roles.foreach(transferRole(coreFrom, coreTo, _))
  }

  def getRelation(core: Any): mutable.Set[Any] = plays(core)

  def getOtherRolesForRole(role: Any): mutable.Set[Any] = {
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

  trait DispatchType {
    // for single method dispatch
    def dispatch[E](on: Any, m: Method): E = m.invoke(on).asInstanceOf[E]

    // for multi-method / multi-argument dispatch
    def dispatch[E, A](on: Any, m: Method, args: Seq[A]): E = {
      val argTypes: Array[Class[_]] = m.getParameterTypes
      val actualArgs: Seq[Any] = args.zip(argTypes).map {
        case (arg: PlayerType[_], tpe: Class[_]) =>
          getRelation(arg.core).find(_.getClass == tpe) match {
            case Some(curRole) => curRole
            // TODO: how to permit this?
            case None => throw new RuntimeException("No role for type '" + tpe + "' found.")
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
  }

  class RoleType[T](val role: T) extends Dynamic with DispatchType {
    def unary_! : RoleType[T] = this

    def applyDynamic[E, A](name: String)
                          (args: A*): E = {
      val core = getCoreFor(role)
      core.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        args match {
          case Nil => return dispatch(core, fm)
          case _ => return dispatch(core, fm, args.toSeq)
        }
      })

      // search all other roles for the given method with name 'name'
      getOtherRolesForRole(core).foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })

      // fallback if the role itself can provide the requested method
      role.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        args match {
          case Nil => return dispatch(role, fm)
          case _ => return dispatch(role, fm, args.toSeq)
        }
      })

      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found!")
    }

    // identity of roles: they don't have their own ID
    override def equals(o: Any) = o match {
      case that: RoleType[_] => that.role == this.role
      case that: PlayerType[_] => that.core == getCoreFor(this)
      case that: Any => that == getCoreFor(this)
    }

    override def hashCode(): Int = role.hashCode()
  }

  class PlayerType[T](val core: T) extends Dynamic with DispatchType {
    def play(role: Any): PlayerType[T] = {
      core match {
        case p: PlayerType[_] => addPlaysRelation(p.core, role)
        case p: Any => addPlaysRelation(p, role)
      }
      this
    }

    def drop(role: Any): PlayerType[T] = {
      removePlaysRelation(core, role)
      this
    }

    def unary_~ : PlayerType[T] = this

    def transfer(role: Any) = new {
      def to(player: Any) {
        transferRole(this, player, role)
      }
    }

    def applyDynamic[E, A](name: String)
                          (args: A*): E = {
      // search all roles the core is playing for the given method with name 'name'
      getRelation(core).foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })

      // search all other roles for the given method with name 'name'
      getOtherRolesForRole(core).foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          args match {
            case Nil => return dispatch(r, fm)
            case _ => return dispatch(r, fm, args.toSeq)
          }
        })
      })

      // fallback if core is providing the requested method itself
      val c = getCoreFor(core)
      c.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        args match {
          case Nil => return dispatch(c, fm)
          case _ => return dispatch(c, fm, args.toSeq)
        }
      })

      core.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        args match {
          case Nil => return dispatch(core, fm)
          case _ => return dispatch(core, fm, args.toSeq)
        }
      })

      // otherwise give up
      throw new RuntimeException(s"No role with method '$name' found! (core: " + core + ")")
    }

    override def equals(o: Any) = o match {
      case that: PlayerType[_] => that.core == this.core
      case that: Any => that == this.core
    }

    override def hashCode(): Int = core.hashCode()
  }

}
