package internal

trait HashImplicit {
  implicit def toHash(wrapped: Any) = new {
    def hash: String = wrapped.hashCode.toString
  }
}