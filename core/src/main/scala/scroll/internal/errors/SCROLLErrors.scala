package scroll.internal.errors

/**
  * Object containing all SCROLL related error.
  */
object SCROLLErrors {

  sealed trait SCROLLError

  sealed trait TypeError

  sealed trait RolePlaying

  case class RolePlayingImpossible(core: String, role: String) extends RolePlaying

  case class TypeNotFound(name: String) extends TypeError

  case class RoleNotFound(forCore: String, target: String, args: String) extends SCROLLError

  sealed trait InvocationError extends SCROLLError

  case class IllegalRoleInvocationSingleDispatch(roleType: String, target: String) extends InvocationError

  case class IllegalRoleInvocationMultipleDispatch(roleType: String, target: String, args: String) extends InvocationError

}
