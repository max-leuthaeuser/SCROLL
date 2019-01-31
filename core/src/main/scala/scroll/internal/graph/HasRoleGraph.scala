package scroll.internal.graph

trait HasRoleGraph {
  // defaults:
  private[internal] var cached: Boolean = true
  private[internal] var checkForCycles: Boolean = true

  private[internal] var plays: ScalaRoleGraph = new CachedScalaRoleGraph(checkForCycles)

  protected def reconfigure(cached: Boolean, checkForCycles: Boolean): Unit = {
    if (cached) {
      plays = new CachedScalaRoleGraph(checkForCycles)
    } else {
      plays = new ScalaRoleGraph(checkForCycles)
    }
    this.cached = cached
    this.checkForCycles = checkForCycles
  }
}
