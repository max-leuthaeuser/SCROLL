package scroll.internal.errors

/**
  * Object containing all SCROLL related error.
  */
object SCROLLErrors {

  sealed trait SCROLLError

  sealed trait TypeError

  case class TypeNotFound(name: String) extends TypeError {
    override def toString: String = s"Type '$name' could not be found!"
  }

  case class RoleNotFound(forCore: String, target: String, args: Seq[Any]) extends SCROLLError {
    override def toString: String = args match {
      case l if l.nonEmpty => s"No role with '$target' could not be found for the player '$forCore' with the following parameters: " + args.map(e => s"'$e'").mkString("(", ", ", ")")
      case _ => s"No role with '$target' could not be found for the player '$forCore'!"
    }
  }

  sealed trait InvocationError extends SCROLLError

  case class IllegalRoleInvocationSingleDispatch(roleType: String, target: String) extends InvocationError {
    override def toString: String = s"'$target' could be not executed on role type '$roleType'!"
  }

  case class IllegalRoleInvocationMultipleDispatch(roleType: String, target: String, args: Seq[Any]) extends InvocationError {
    override def toString: String = args match {
      case l if l.nonEmpty => s"'$target' could not be executed on role type '$roleType' with the following parameters: " + args.map(e => s"'$e'").mkString("(", ", ", ")")
      case _ => s"'$target' could not be executed on role type '$roleType'!"
    }
  }

}
