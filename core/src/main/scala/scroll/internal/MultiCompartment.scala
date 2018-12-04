package scroll.internal

import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.errors.SCROLLErrors.SCROLLError
import scroll.internal.support.DispatchQuery
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag

/**
  * This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  * In contrast to the normal Compartment, in case of ambiguities all role methods will be called in sequence.
  */
trait MultiCompartment extends ICompartment {

  implicit def either2SeqTOrException[T](either: Either[_, Seq[Either[_, T]]]): Seq[T] = either.fold(
    left => throw new RuntimeException(left.toString),
    right => right.map(either2TorException)
  )

  override def newPlayer[W <: AnyRef : ClassTag](obj: W): MultiPlayer[W] = {
    require(null != obj)
    new MultiPlayer(obj)
  }

  implicit class MultiPlayer[W <: AnyRef : ClassTag](override val wrapped: W) extends IPlayer[W, MultiPlayer[W]](wrapped) {

    def applyDynamic[E](name: String)(args: Any*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDispatchQuery(dispatchQuery, wrapped).map { r =>
        (r, ReflectiveHelper.findMethod(r, name, args))
      }.collect {
        case (r, Some(m)) => dispatch[E](r, m, args: _*)
      } match {
        case Nil => Left(RoleNotFound(wrapped.toString, name, args))
        case l => Right(l)
      }

    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDispatchQuery(dispatchQuery, wrapped).collect {
        case r if ReflectiveHelper.hasMember(r, name) => ReflectiveHelper.propertyOf[E](r, name)
      } match {
        case Nil => Left(RoleNotFound(wrapped.toString, name, Seq.empty))
        case l => Right(l.map(Right(_)))
      }

    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit =
      applyDispatchQuery(dispatchQuery, wrapped).view.
        filter(ReflectiveHelper.hasMember(_, name)).
        foreach(ReflectiveHelper.setPropertyOf(_, name, value))

  }

}
