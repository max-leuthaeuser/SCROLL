package scroll.examples.sync

import scroll.examples.sync.roles.IRoleManager

class ConstructionContainer {
  private var startElement: Boolean = false
  private var constructed: Boolean = false;
  private var playerInstance: PlayerSync = null;
  private var managerInstance: IRoleManager = null;
  
  def isStartElement(): Boolean = startElement
  
  def isConstructed(): Boolean = constructed
  
  def getPlayerInstance(): PlayerSync = playerInstance
  
  def getManagerInstance(): IRoleManager = managerInstance
  
  def fillContainer(start: Boolean, con: Boolean, play: PlayerSync, man: IRoleManager): Unit = {
    startElement = start
    constructed = con
    playerInstance = play
    managerInstance = man
  }
}