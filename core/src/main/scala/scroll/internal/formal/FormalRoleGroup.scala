package scroll.internal.formal

case class FormalRoleGroup(rolegroups: List[Any], lower: Int, upper: Int) {
  assert(0 <= lower && lower <= upper)
}