package scroll.internal.formal

/** Class representation of role groups.
  *
  * @param rolegroups
  *   nested role groups if any
  * @param lower
  *   lower bound as int
  * @param upper
  *   upper bound as int
  */
final case class FormalRoleGroup(rolegroups: List[AnyRef], lower: Int, upper: Int) {
  assert(lower >= 0 && lower <= upper)
}
