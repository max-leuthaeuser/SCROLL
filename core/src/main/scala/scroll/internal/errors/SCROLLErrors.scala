package scroll.internal.errors

/**
  * Object containing all SCROLL related error.
  */
object SCROLLErrors {

  sealed trait SCROLLError

  sealed trait TypeError

  final case class TypeNotFound(tpe: Class[_]) extends TypeError {
    override def toString: String = s"Type '$tpe' could not be found!"
  }

  final case class RoleNotFound(forCore: AnyRef, target: String, args: Seq[Any]) extends SCROLLError {
    override def toString: String = args match {
      case l if l.nonEmpty =>
        s"No role with '$target' could not be found for the player '$forCore' with the following parameters: " +
          args.map(e => s"'$e'").mkString("(", ", ", ")")
      case _ => s"No role with '$target' could not be found for the player '$forCore'!"
    }
  }

  sealed trait InvocationError extends SCROLLError

  final case class IllegalRoleInvocationDispatch(roleType: AnyRef, target: String, args: Seq[Any]) extends InvocationError {
    override def toString: String = args match {
      case l if l.nonEmpty =>
        s"'$target' could not be executed on role type '$roleType' with the following parameters: " +
          args.map(e => s"'$e'").mkString("(", ", ", ")")
      case _ => s"'$target' could not be executed on role type '$roleType'!"
    }
  }

}
