package players

import roles.RoleManager

object PlayerConversion
{
  implicit def anyToPlayer[T](any: T) = new Player[T](any)
}

// TODO: test methods with arguments
// TODO: transfer roles
// TODO: identity of player objects
class Player[T](private val core: T) extends Dynamic
{
  def play(role: Any): Player[T] =
  {
    core match {
      case p: Player[_] => RoleManager.addPlaysRelation(p.core, role)
      case p: Any => RoleManager.addPlaysRelation(p, role)
    }
    this
  }

  def drop(role: Any): Player[T] =
  {
    RoleManager.removePlaysRelation(core, role)
    this
  }

  def unary_~ : Player[T] = this

  def transfer(role: Any) = new
    {
      def to(player: Any)
      {
        RoleManager.transferRole(this, player, role)
      }
    }

  def applyDynamic[E](name: String)
    (args: Any*): E =
  {
    // println(s"method '$name' called with arguments ${args.mkString("'", "', '", "'")}")
    // search all roles for the given method with name 'name'
    RoleManager.getRelation(core).foreach(r => {
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
