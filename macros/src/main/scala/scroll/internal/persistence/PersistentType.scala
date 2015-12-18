package scroll.internal.persistence

import scala.reflect.runtime.universe._

case class PersistentType(t: String)

object PersistentType {
  def apply[T: WeakTypeTag](): PersistentType = PersistentType(weakTypeOf[T].toString)
}
