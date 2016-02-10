package scroll.internal

import scroll.internal.graph.KiamaScalaRoleGraph

trait KiamaCompartment extends Compartment {
  plays = new KiamaScalaRoleGraph()
}
