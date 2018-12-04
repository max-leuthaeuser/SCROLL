package scroll.internal.support

import scroll.internal.ICompartment

import scala.reflect.ClassTag

trait PlayerEquality {
  self: ICompartment =>

  def equalsPlayer[W <: AnyRef : ClassTag](a: IPlayer[W, _], b: IPlayer[W, _]): Boolean = (coreFor(a.wrapped), coreFor(b.wrapped)) match {
    case (cl1, cl2) if cl1 equals cl2 => true
    case (_ :+ last, head +: Nil) if head == last => true
    case (head +: Nil, _ :+ last) if head == last => true
    case _ => false
  }

  def equalsAny[W <: AnyRef : ClassTag](a: IPlayer[W, _], b: Any): Boolean = coreFor(a.wrapped) match {
    case head +: Nil => head == b
    case _ :+ last => last == b
  }
}
