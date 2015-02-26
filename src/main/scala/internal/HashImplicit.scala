package internal

import scala.language.implicitConversions

trait HashImplicit {
  implicit def toHash(wrapped: Any): Object {def hash: String} = new {
    def hash: String = wrapped.hashCode.toString
  }
}