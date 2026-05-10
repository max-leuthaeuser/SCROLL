package scroll.internal.errors

/** Object containing all SCROLL related error.
  */
object SCROLLErrors {

  sealed trait SCROLLError extends RuntimeException

  sealed trait TypeError extends SCROLLError

  sealed trait ValidationError extends RuntimeException {
    override def getMessage: String = toString
  }

  sealed trait ConstraintError extends ValidationError

  final case class RoleImplicationConstraintViolation(player: AnyRef, requiredRole: String) extends ConstraintError {

    override def toString: String =
      s"Role implication constraint violation: '$player' should play role '$requiredRole', but it does not!"

  }

  final case class RoleEquivalenceConstraintViolation(player: AnyRef, requiredRole: String) extends ConstraintError {

    override def toString: String =
      s"Role equivalence constraint violation: '$player' should play role '$requiredRole', but it does not!"

  }

  final case class RoleProhibitionConstraintViolation(player: AnyRef, prohibitedRole: String) extends ConstraintError {

    override def toString: String =
      s"Role prohibition constraint violation: '$player' plays role '$prohibitedRole', but it is not allowed to do so!"

  }

  final case class RoleRestrictionViolation(player: AnyRef, role: AnyRef) extends ValidationError {

    override def toString: String =
      s"Role '$role' can not be played by '$player' due to the active role restrictions!"

  }

  sealed trait RoleGroupError extends ValidationError

  final case class RoleGroupOccurrenceCardinalityViolation(
    groupName: String,
    roleTypes: Seq[String],
    actual: Int,
    min: Int,
    max: String
  ) extends RoleGroupError {

    override def toString: String =
      s"Occurrence cardinality in role group '$groupName' violated! " +
        s"Roles '$roleTypes' are played $actual times but should be between $min and $max."

  }

  final case class MissingRoleGroupConstraint(groupName: String, roleType: String) extends RoleGroupError {
    override def toString: String = s"Constraints for role group '$groupName' do not contain '$roleType'!"
  }

  final case class UnsolvableRoleGroupConstraint(groupName: String) extends RoleGroupError {
    override def toString: String = s"Constraint set of role group '$groupName' unsolvable!"
  }

  final case class RoleGroupInnerCardinalityViolation(groupName: String) extends RoleGroupError {
    override def toString: String = s"Constraint set for inner cardinality of role group '$groupName' violated!"
  }

  final case class InvalidRoleGroupEntry() extends RoleGroupError {
    override def toString: String = "Role groups can only contain a list of types or role groups itself!"
  }

  final case class InvalidRoleGroupConstraint(groupName: String, min: Int, max: String) extends RoleGroupError {
    override def toString: String = s"Role group constraint of ($min, $max) for role group '$groupName' not possible!"
  }

  final case class DuplicateRoleGroup(groupName: String) extends RoleGroupError {
    override def toString: String = s"The RoleGroup $groupName was already added!"
  }

  final case class TypeNotFound(tpe: Class[?]) extends TypeError {
    override def toString: String = s"Type '$tpe' could not be found!"
  }

  final case class RoleNotFound(forCore: AnyRef, target: String, args: Seq[Any]) extends SCROLLError {

    override def toString: String =
      args match {
        case l if l.nonEmpty =>
          s"No role with '$target' could not be found for the player '$forCore' with the following parameters: " +
            args.map(e => s"'$e'").mkString("(", ", ", ")")
        case _ => s"No role with '$target' could not be found for the player '$forCore'!"
      }

  }

  sealed trait InvocationError extends SCROLLError

  final case class IllegalRoleInvocationDispatch(roleType: AnyRef, target: String, args: Seq[Any])
      extends InvocationError {

    override def toString: String =
      args match {
        case l if l.nonEmpty =>
          s"'$target' could not be executed on role type '$roleType' with the following parameters: " +
            args.map(e => s"'$e'").mkString("(", ", ", ")")
        case _ => s"'$target' could not be executed on role type '$roleType'!"
      }

  }

}
