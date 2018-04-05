package scroll.examples.sync.roles

import scroll.examples.sync.PlayerSync
import scala.collection.mutable.ListBuffer
import scroll.examples.sync.ConstructionContainer

trait IConstructor {
  
  protected var containers = ListBuffer[ConstructionContainer]()
  
  protected def createContainerElement(start: Boolean, con: Boolean, play: PlayerSync, man: IRoleManager): Unit = {
    if (play == null)
      return
    var cc = new ConstructionContainer()
    cc.fillContainer(start, con, play, man)
    containers = containers :+ cc
  }
  
  def construct(comp: PlayerSync, man: IRoleManager) : Unit
}