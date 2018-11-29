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
trait MultiCompartment extends Compartment {

  implicit def either2SeqTOrException[T](either: Either[_, Seq[Either[_, T]]]): Seq[T] = either.fold(
    left => throw new RuntimeException(left.toString),
    right => right.map(either2TorException)
  )

  implicit class MultiPlayer[T <: AnyRef : ClassTag](override val wrapped: T) extends IPlayer[T](wrapped) with Dynamic with SCROLLDispatchable {

    override def unary_+ : MultiPlayer[T] = this

    override def play[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      require(null != role)
      wrapped match {
        case p: MultiPlayer[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: AnyRef => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case p => throw new RuntimeException(s"Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles! You tried it with '$p'.")
      }
      this
    }

    override def <+>[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = play(role)

    override def drop[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    override def <->[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = drop(role)

    override def remove(): Unit = plays.removePlayer(this.wrapped)

    override def roles(): Seq[AnyRef] = plays.roles(this.wrapped)

    override def facets(): Seq[Enumeration#Value] = plays.facets(this.wrapped)

    override def predecessors(): Seq[AnyRef] = plays.predecessors(this.wrapped)

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

    override def equals(o: Any): Boolean = o match {
      case other: MultiPlayer[_] => PlayerEquality.equalsPlayer(this, other)
      case other: Any => PlayerEquality.equalsAny(this, other)
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}
