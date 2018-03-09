package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync

trait IConstructor {
  def construct(comp: PlayerSync, man: IRoleManager) : Unit
}