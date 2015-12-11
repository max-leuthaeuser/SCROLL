package scroll.examples

import scroll.internal.Compartment
import scroll.internal.support.Coroutines
import scala.concurrent.blocking

object CoroutinesExample extends App {
  val sleep = 100

  new Compartment with Coroutines {
    coroutine {
      while (true) {
        println("Hi!")
        blocking(Thread.sleep(sleep))
        yld
        println("cont'd!")
      }
    }

    coroutine {
      while (true) {
        println("Ho!")
        blocking(Thread.sleep(sleep))
        yld
        println("cont'd!")
      }
    }

    coroutine {
      while (true) {
        println("Ha!")
        blocking(Thread.sleep(sleep))
        yld
        println("cont'd!")
      }
    }

    for (_ <- 0 until 6)
      yieldAll
  }
}
