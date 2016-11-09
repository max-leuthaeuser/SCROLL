package scroll.internal.util

import com.google.common.cache.{Cache, CacheBuilder}

/**
  * Support for memoization, encapsulating common behaviour of memoised
  * entities and a general reset mechanism for all such entities.
  */
trait Memoiser {

  /**
    * Common interface for encapsulation of memoization for a single memoised
    * entity backed by a configurable cache.
    */
  trait MemoisedBase[T, U] {

    /**
      * The memo table.
      */
    def memo: Cache[AnyRef, AnyRef]

    /**
      * Duplicate an entry if possible. If `t1` has a memoised value associated
      * with it, set the value associated with `t2` to the same value. If there
      * is no value associated with `t1`, do nothing.
      */
    def dup(t1: T, t2: T): Unit = {
      val u = memo.getIfPresent(t1).asInstanceOf[U]
      if (u != null)
        put(t2, u)
    }

    /**
      * Return the value stored at key `t` as an option.
      */
    def get(t: T): Option[U] = Option(memo.getIfPresent(t).asInstanceOf[U])

    /**
      * Return the value stored at key `t` if there is one, otherwise
      * return `u`. `u` is only evaluated if necessary.
      */
    def getWithDefault(t: T, u: => U): U = get(t).getOrElse(u)

    /**
      * Has the value at `t` already been computed or not? By default, does
      * the memo table contain a value for `t`?
      */
    def hasBeenComputedAt(t: T): Boolean = get(t).isDefined

    /**
      * Store the value `u` under the key `t`.
      */
    def put(t: T, u: U): Unit = {
      memo.put(t.asInstanceOf[AnyRef], u.asInstanceOf[AnyRef])
    }

    /**
      * Store the value `u` under the key `t` if `t` does not already have an
      * associated value. `u` is only evaluated if necessary.
      */
    def putIfNotPresent(t: T, u: => U): Unit = {
      if (!hasBeenComputedAt(t))
        put(t, u)
    }

    /**
      * Immediately reset the memo table.
      */
    def reset(): Unit = {
      memo.invalidateAll()
    }

    /**
      * Immediately reset the memo table at `t`.
      */
    def resetAt(t: T): Unit = {
      memo.invalidate(t)
    }

    /**
      * The number of entries in the memo table.
      */
    def size(): Long = memo.size
  }

  /**
    * A memoised entity that uses equality to compare keys.
    */
  trait Memoised[T, U] extends MemoisedBase[T, U] {
    val memo: Cache[AnyRef, AnyRef] = CacheBuilder.newBuilder.build()
  }

  /**
    * A memoised entity that weakly holds onto its keys and uses identity
    * to compare them.
    */
  trait IdMemoised[T, U] extends MemoisedBase[T, U] {
    val memo: Cache[AnyRef, AnyRef] = CacheBuilder.newBuilder.weakKeys.build()
  }

}