package scroll.examples

import scroll.internal.Compartment
import scroll.internal.util.Log.info

object CoroutinesExample extends App {
  new Compartment {
    coroutine {
      while (true) {
        info("Hi!")
        yld
        info("cont'd!")
      }
    }

    coroutine {
      while (true) {
        info("Ho!")
        yld
        info("cont'd!")
      }
    }

    coroutine {
      while (true) {
        info("Ha!")
        yld
        info("cont'd!")
      }
    }

    for (_ <- 0 until 6)
      yieldAll
  }
}
