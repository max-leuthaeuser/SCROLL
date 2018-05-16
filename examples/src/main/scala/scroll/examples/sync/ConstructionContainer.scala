package scroll.examples.sync

import scroll.examples.sync.roles.IRoleManager

/**
 * Helper class for all construction processes to manage standard work loads.
 */
class ConstructionContainer {
  private var startElement: Boolean = false
  private var constructed: Boolean = false;
  private var playerInstance: PlayerSync = null;
  private var managerInstance: IRoleManager = null;
  
  /**
   * Returns true if it is the start construction element.
   */
  def isStartElement(): Boolean = startElement
  
  /**
   * Return true if it is new constructed.
   */
  def isConstructed(): Boolean = constructed
  
  /**
   * Get the PlayerSync instance of this element.
   */
  def getPlayerInstance(): PlayerSync = playerInstance
  
  /**
   * Get the RoleManager instance of this element
   */
  def getManagerInstance(): IRoleManager = managerInstance
  
  /**
   * Fills all elements at the beginning.
   */
  def fillContainer(start: Boolean, con: Boolean, play: PlayerSync, man: IRoleManager): Unit = {
    startElement = start
    constructed = con
    playerInstance = play
    managerInstance = man
  }
}