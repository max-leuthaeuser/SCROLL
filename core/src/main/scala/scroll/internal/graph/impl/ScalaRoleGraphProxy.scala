package scroll.internal.graph.impl

import scroll.internal.graph.RoleGraph
import scroll.internal.graph.RoleGraphProxyApi

class ScalaRoleGraphProxy extends RoleGraphProxyApi {
  override private[internal] var plays: RoleGraph = new CachedScalaRoleGraph()

  override def reconfigure(cached: Boolean, checkForCycles: Boolean): Unit = if (cached) {
    plays = CachedScalaRoleGraph.copyFrom(plays.asInstanceOf[ScalaRoleGraph], checkForCycles)
  } else {
    plays = ScalaRoleGraph.copyFrom(plays.asInstanceOf[ScalaRoleGraph], checkForCycles)
  }

}
