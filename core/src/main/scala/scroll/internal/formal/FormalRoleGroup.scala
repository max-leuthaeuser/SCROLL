package scroll.internal.formal

/**
 * Class representation of role groups.
 *
 * @param rolegroups nested role groups if any
 * @param lower lower bound as int
 * @param upper upper bound as int
 */
case class FormalRoleGroup(rolegroups: List[Any], lower: Int, upper: Int) {
  assert(0 <= lower && lower <= upper)
}