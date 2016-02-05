package scroll.internal

import scroll.internal.graph.CachedScalaRoleGraph

trait CachedCompartment extends Compartment {
  plays = new CachedScalaRoleGraph()
}
