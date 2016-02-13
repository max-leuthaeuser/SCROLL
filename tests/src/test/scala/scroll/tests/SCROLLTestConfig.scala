package scroll.tests

object SCROLLTestConfig {

  sealed trait Backend

  object JGRAPHT extends Backend

  object KIAMA extends Backend

  object CACHED extends Backend

}

trait SCROLLTestConfig {

  import SCROLLTestConfig._

  protected var backend: Backend = JGRAPHT
}
