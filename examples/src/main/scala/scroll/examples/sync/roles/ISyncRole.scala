package scroll.examples.sync.roles

import scroll.examples.sync.ISyncCompartment

trait ISyncRole {
  def getOuterCompartment(): ISyncCompartment
}