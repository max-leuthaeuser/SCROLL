package scroll.examples.sync

import scroll.examples.sync.roles.IRoleManager

class IntegrationContainer (_newPlayer: PlayerSync, _newManager: IRoleManager, _oldPlayer: PlayerSync, _oldManager: IRoleManager) {
  /**
   * Get the new PlayerSync instance of this element.
   */
  def getNewPlayerInstance(): PlayerSync = _newPlayer
  
  /**
   * Get the new RoleManager instance of this element
   */
  def getNewManagerInstance(): IRoleManager = _newManager
  
  /**
   * Get the old PlayerSync instance of this element.
   */
  def getOldPlayerInstance(): PlayerSync = _oldPlayer
  
  /**
   * Get the old RoleManager instance of this element
   */
  def getOldManagerInstance(): IRoleManager = _oldManager 
}