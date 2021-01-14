package scroll.internal.util

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache

/**
  * Support for building caches using Guava caches.
  */
object Memoiser {

  /**
    * Builds a cache, which either returns an already-loaded value for a given key or atomically
    * computes or retrieves it using the supplied { @code supplier }. If another thread is currently
    * loading the value for this key, simply waits for that thread to finish and returns its loaded
    * value. Note that multiple threads can concurrently load values for distinct keys.
    *
    * @param supplier the function to be used for loading values; must never return { @code null}
    * @return a cache loader that loads values by passing each key to { @code supplier}
    */
  def buildCache[K <: AnyRef, V <: AnyRef](supplier: K => V): LoadingCache[K, V] =
    CacheBuilder.newBuilder().build[K, V](CacheLoader.from((k: K) => supplier(k)))

}
