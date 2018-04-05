package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.ConstructionContainer

trait IConstructor {
  
  protected var containers = ListBuffer[ConstructionContainer]()
  
  def construct(comp: PlayerSync, man: IRoleManager) : Unit
}