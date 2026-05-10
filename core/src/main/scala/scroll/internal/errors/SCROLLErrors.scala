package scroll.internal.errors

/** Object containing all SCROLL related error.
  */
object SCROLLErrors {

  sealed trait SCROLLError extends RuntimeException {
    override def getMessage: String = toString
  }

  sealed trait TypeError extends SCROLLError

  /** Base type for validation failures raised by constraints, restrictions, and role groups.
    */
  sealed trait ValidationError extends RuntimeException {
    override def getMessage: String = toString
  }

  sealed trait ConstraintError extends ValidationError

  /** Raised when a role implication constraint requires another role that is currently missing.
    */
  final case class RoleImplicationConstraintViolation(player: AnyRef, requiredRole: String) extends ConstraintError {

    override def toString: String =
      s"Role implication constraint violation: '$player' should play role '$requiredRole', but it does not!"

  }

  /** Raised when an equivalence constraint requires another role that is currently missing.
    */
  final case class RoleEquivalenceConstraintViolation(player: AnyRef, requiredRole: String) extends ConstraintError {

    override def toString: String =
      s"Role equivalence constraint violation: '$player' should play role '$requiredRole', but it does not!"

  }

  /** Raised when a player holds a role that is forbidden by a prohibition constraint.
    */
  final case class RoleProhibitionConstraintViolation(player: AnyRef, prohibitedRole: String) extends ConstraintError {

    override def toString: String =
      s"Role prohibition constraint violation: '$player' plays role '$prohibitedRole', but it is not allowed to do so!"

  }

  /** Raised when a player attempts to play a role that is blocked by active role restrictions.
    */
  final case class RoleRestrictionViolation(player: AnyRef, role: AnyRef) extends ValidationError {

    override def toString: String =
      s"Role '$role' can not be played by '$player' due to the active role restrictions!"

  }

  sealed trait RoleGroupError extends ValidationError

  sealed trait RelationshipError extends ValidationError

  sealed trait ReflectionError extends SCROLLError

  sealed trait FormalModelError extends RuntimeException {
    override def getMessage: String = toString
  }

  sealed trait GraphError extends SCROLLError

  sealed trait PlayerError extends SCROLLError

  /** Raised when the occurrence cardinality of a role group is outside the configured bounds.
    */
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

  /** Raised when solving a role-group constraint references an unknown role type variable.
    */
  final case class MissingRoleGroupConstraint(groupName: String, roleType: String) extends RoleGroupError {
    override def toString: String = s"Constraints for role group '$groupName' do not contain '$roleType'!"
  }

  /** Raised when a role-group constraint model cannot be solved at all.
    */
  final case class UnsolvableRoleGroupConstraint(groupName: String) extends RoleGroupError {
    override def toString: String = s"Constraint set of role group '$groupName' unsolvable!"
  }

  /** Raised when the inner cardinality of a role group is violated by the current role assignments.
    */
  final case class RoleGroupInnerCardinalityViolation(groupName: String) extends RoleGroupError {
    override def toString: String = s"Constraint set for inner cardinality of role group '$groupName' violated!"
  }

  /** Raised when a role-group definition contains unsupported entries.
    */
  final case class InvalidRoleGroupEntry() extends RoleGroupError {
    override def toString: String = "Role groups can only contain a list of types or role groups itself!"
  }

  /** Raised when a role-group cardinality definition is not representable by the supported constraint kinds.
    */
  final case class InvalidRoleGroupConstraint(groupName: String, min: Int, max: String) extends RoleGroupError {
    override def toString: String = s"Role group constraint of ($min, $max) for role group '$groupName' not possible!"
  }

  /** Raised when a role group with the same name is registered more than once.
    */
  final case class DuplicateRoleGroup(groupName: String) extends RoleGroupError {
    override def toString: String = s"The RoleGroup $groupName was already added!"
  }

  /** Raised when a relationship expects at least one match but none can be found.
    */
  final case class EmptyRelationshipMultiplicityViolation(relationshipName: String) extends RelationshipError {

    override def toString: String =
      s"With left multiplicity for '$relationshipName' of '*', the resulting role set should not be empty!"

  }

  /** Raised when a relationship expects an exact number of matches but gets a different size.
    */
  final case class ConcreteRelationshipMultiplicityViolation(relationshipName: String, expectedSize: Ordered[Int])
      extends RelationshipError {

    override def toString: String =
      s"With a concrete multiplicity for '$relationshipName' of '$expectedSize' the resulting role set should have the same size!"

  }

  /** Raised when a relationship expects a bounded range of matches but gets a size outside that range.
    */
  final case class RangeRelationshipMultiplicityViolation(
    relationshipName: String,
    lowerBound: Ordered[Int],
    upperBound: String
  ) extends RelationshipError {

    override def toString: String =
      s"With a multiplicity for '$relationshipName' from '$lowerBound' to '$upperBound', the resulting role set size should be in between!"

  }

  /** Raised when a relationship uses a multiplicity shape that is not supported by the runtime.
    */
  final case class UnsupportedRelationshipMultiplicity() extends RelationshipError {
    override def toString: String = "This multiplicity is not allowed!"
  }

  /** Raised when reflective field lookup fails.
    */
  final case class ReflectiveFieldNotFound(owner: Class[?], fieldName: String) extends ReflectionError {
    override def toString: String = s"Field '$fieldName' not found on '$owner'!"
  }

  /** Raised when reflective method lookup by name fails.
    */
  final case class ReflectiveMethodNotFound(target: AnyRef, methodName: String) extends ReflectionError {
    override def toString: String = s"Function with name '$methodName' not found on '$target'!"
  }

  /** Raised when a role graph operation would introduce a cycle.
    */
  final case class CyclicRolePlayingRelationshipFound() extends GraphError {
    override def toString: String = "Cyclic role-playing relationship found!"
  }

  /** Raised when a specific player would become part of a cyclic role graph.
    */
  final case class CyclicRolePlayingRelationshipForPlayer(player: AnyRef) extends GraphError {
    override def toString: String = s"Cyclic role-playing relationship for player '$player' found!"
  }

  /** Raised when the CROM meta-model cannot be loaded.
    */
  final case class CROMMetaModelLoadFailure() extends FormalModelError {
    override def toString: String = "Meta-Model for CROM could not be loaded!"
  }

  /** Raised when a CROI relationship references a role without a corresponding plays entry.
    */
  final case class RoleNotPlayedInCROI(role: AnyRef) extends FormalModelError {
    override def toString: String = s"The given role '$role' is not played in the CROI!"
  }

  /** Raised when something other than an object/player tries to participate in role playing.
    */
  final case class InvalidRolePlayer() extends PlayerError {
    override def toString: String = "Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles!"
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
