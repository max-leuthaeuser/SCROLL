package scroll.internal.compartment.impl

import scroll.internal.dispatch.DispatchQuery
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.SCROLLError
import scroll.internal.util.ReflectiveHelper

import java.lang.reflect.Method
import scala.reflect.ClassTag

/** This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  * In contrast to the normal Compartment, in case of ambiguities all role methods will be called in sequence.
  */
trait MultiCompartment extends AbstractCompartment {

  implicit def either2SeqTOrException[T](either: Either[_, Seq[Either[_, T]]]): Seq[T] =
    either.fold(
      left => throw new RuntimeException(left.toString),
      right => right.map(either2TorException)
    )

  override def newPlayer[W <: AnyRef: ClassTag](obj: W): MultiPlayer[W] = {
    require(null != obj)
    new MultiPlayer(obj)
  }

  implicit class MultiPlayer[W <: AnyRef: ClassTag](override val wrapped: W)
      extends IPlayer[W, MultiPlayer[W]](wrapped) {

    def applyDynamic[E](name: String)(
      args:                   Any*
    )(using dispatchQuery:    DispatchQuery = DispatchQuery()): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDispatchQuery(dispatchQuery, wrapped)
        .map { (r: AnyRef) =>
          (r, ReflectiveHelper.findMethod(r, name, args.toSeq))
        }
        .collect { case (r: AnyRef, Some(m: Method)) =>
          dispatch[E](r, m, args.toSeq)
        } match {
        case Nil => Left(RoleNotFound(wrapped, name, args.toSeq))
        case l   => Right(l)
      }

    def applyDynamicNamed[E](name: String)(
      args:                        (String, Any)*
    )(using dispatchQuery:         DispatchQuery = DispatchQuery()): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDynamic[E](name)(args.map(_._2): _*) (using dispatchQuery)

    def selectDynamic[E](
      name:                        String
    )(using dispatchQuery:         DispatchQuery = DispatchQuery()): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDispatchQuery(dispatchQuery, wrapped).collect {
        case r: AnyRef if ReflectiveHelper.hasMember(r, name) => ReflectiveHelper.propertyOf[E](r, name)
      } match {
        case Nil => Left(RoleNotFound(wrapped, name, Seq.empty[Any]))
        case l   => Right(l.map(Right(_)))
      }

    def updateDynamic(name: String)(value: Any)(using dispatchQuery: DispatchQuery = DispatchQuery()): Unit =
      applyDispatchQuery(dispatchQuery, wrapped).view
        .filter(ReflectiveHelper.hasMember(_, name))
        .foreach(ReflectiveHelper.setPropertyOf(_, name, value))

    def hashCode()(using dispatchQuery: DispatchQuery = DispatchQuery()): Seq[Int] =
      applyDynamic("hashCode")() (using dispatchQuery)

    def toString()(using dispatchQuery: DispatchQuery = DispatchQuery()): Seq[String] =
      applyDynamic("toString")() (using dispatchQuery)
  }

}
