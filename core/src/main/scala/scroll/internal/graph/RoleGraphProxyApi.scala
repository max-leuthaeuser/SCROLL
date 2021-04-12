package scroll.internal.graph

/** API for defining a proxy to a [[RoleGraph]] allowing to reconfigure it on-the-fly.
  */
trait RoleGraphProxyApi {
  private[internal] var plays: RoleGraph

  /** Reconfigure the underlying [[RoleGraph]] making it cached/non-cached
    * or using/not using cycle detection.
    *
    * @param cached         either using a [[scroll.internal.graph.impl.CachedScalaRoleGraph]]
    *                       or a non-cached [[scroll.internal.graph.impl.ScalaRoleGraph]].
    * @param checkForCycles use/not use cycle detection
    */
  def reconfigure(cached: Boolean, checkForCycles: Boolean): Unit
}
