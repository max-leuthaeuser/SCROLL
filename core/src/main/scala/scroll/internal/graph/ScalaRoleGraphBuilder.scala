package scroll.internal.graph

object ScalaRoleGraphBuilder {
  private var _cached: Boolean = true

  def configure(cached: Boolean): ScalaRoleGraphBuilder.type = {
    _cached = cached
    this
  }

  def build: ScalaRoleGraph = if (_cached) {
    new CachedScalaRoleGraph()
  } else {
    new ScalaRoleGraph()
  }
}
