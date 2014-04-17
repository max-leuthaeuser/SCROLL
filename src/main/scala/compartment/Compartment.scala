package compartment

import scala.collection.mutable

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

  def getCoreFor(role: Any): Any = plays.foreach {
    case (
      k,
      v) => if (v.contains(role)) return k
  }

  class Role[T](private val role: T) extends Dynamic
  {
    def unary_! : Role[T] = this

    def applyDynamic[E](name: String)
      (args: Any*): E =
    {
      val core = getCoreFor(role)
      core.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
        val tpe = fm.getAnnotatedReturnType.getType
        args match {
          case Nil => return fm.invoke(core).asInstanceOf[E]
          case _ => return fm.invoke(core, args).asInstanceOf[E]
        }
      })
      throw new RuntimeException(s"No role with method '$name' found!")
    }
  }

  // TODO: test methods with arguments
  // TODO: identity of player objects
  class Player[T](private val core: T) extends Dynamic
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

    def applyDynamic[E](name: String)
      (args: Any*): E =
    {
      // println(s"method '$name' called with arguments ${args.mkString("'", "', '", "'")}")
      // search all roles for the given method with name 'name'
      getRelation(core).foreach(r => {
        r.getClass.getDeclaredMethods.find(m => m.getName.equals(name)).foreach(fm => {
          val tpe = fm.getAnnotatedReturnType.getType
          args match {
            case Nil => return fm.invoke(r).asInstanceOf[E]
            case _ => return fm.invoke(r, args).asInstanceOf[E]
          }
        })
      })
      throw new RuntimeException(s"No role with method '$name' found!")
    }
  }

}
