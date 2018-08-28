package scroll.internal.graph

object ScalaRoleGraphBuilder {
  private var _cached: Boolean = true
  private var _checkForCycles: Boolean = true

  def cached(cached: Boolean): ScalaRoleGraphBuilder.type = {
    _cached = cached
    this
  }

  def checkForCycles(checkForCycles: Boolean): ScalaRoleGraphBuilder.type = {
    _checkForCycles = checkForCycles
    this
  }

  def build: ScalaRoleGraph = if (_cached) {
    new CachedScalaRoleGraph(checkForCycles = _checkForCycles)
  } else {
    new ScalaRoleGraph(checkForCycles = _checkForCycles)
  }
}
