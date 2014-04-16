package roles

object RoleConversions
{
  implicit def anyToRole[T](any: T) = new Role[T](any)
}

class Role[T](private val role: T) extends Dynamic
{
  def unary_! : Role[T] = this

  def applyDynamic[E](name: String)
    (args: Any*): E =
  {
    val core = RoleManager.getCoreFor(role)
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