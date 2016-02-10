package scroll.tests

object SCROLLTestConfig extends Enumeration {
  type Backend = Value
  val JGRAPHT, CACHED, KIAMA = Value
}

trait SCROLLTestConfig {

  import SCROLLTestConfig._

  protected var backend: Backend = JGRAPHT
}
