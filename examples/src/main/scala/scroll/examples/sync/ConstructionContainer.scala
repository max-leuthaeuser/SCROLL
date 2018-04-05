package scroll.examples.sync

import scroll.examples.sync.roles.IRoleManager

class ConstructionContainer {
  var constructed: Boolean = false;
  var playerInstance: PlayerSync = null;
  var managerInstance: IRoleManager = null;
  
  def fillContainer(con: Boolean, play: PlayerSync, man: IRoleManager): Unit = {
    constructed = con
    playerInstance = play
    managerInstance = man
  }
}