package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync

trait IIntegrator {
  def integrate(comp: PlayerSync) : Unit
}