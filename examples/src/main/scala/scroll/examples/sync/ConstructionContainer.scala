package scroll.examples.sync

import scroll.examples.sync.roles.IRoleManager

/**
  * Helper class for all construction processes to manage standard work loads.
  */
class ConstructionContainer(_startElement: Boolean, _constructed: Boolean, _player: PlayerSync, _manager: IRoleManager) {
  /**
    * Returns true if it is the start construction element.
    */
  def isStartElement(): Boolean = _startElement

  /**
    * Return true if it is new constructed.
    */
  def isConstructed(): Boolean = _constructed

  /**
    * Get the PlayerSync instance of this element.
    */
  def getPlayerInstance(): PlayerSync = _player

  /**
    * Get the RoleManager instance of this element
    */
  def getManagerInstance(): IRoleManager = _manager
}