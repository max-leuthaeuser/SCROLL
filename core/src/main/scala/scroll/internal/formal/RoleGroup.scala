package scroll.internal.formal

case class RoleGroup(rolegroups: List[Any], lower: Int, upper: Int) {
  assert(0 <= lower && lower <= upper)
}