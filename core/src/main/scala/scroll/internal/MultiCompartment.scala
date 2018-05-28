package scroll.internal

import scroll.internal.errors.SCROLLErrors.SCROLLError
import scroll.internal.errors.SCROLLErrors.RoleNotFound
import scroll.internal.support.DispatchQuery
import scroll.internal.util.ReflectiveHelper

import scala.reflect.ClassTag

/**
  * This Trait allows for implementing an objectified collaboration with a limited number of participating roles and a fixed scope.
  * In contrast to the normal Compartment, in case of ambiguities all role methods will be called in sequence.
  */
trait MultiCompartment extends Compartment {

  implicit class MultiPlayer[T <: AnyRef : ClassTag](override val wrapped: T) extends IPlayer[T](wrapped) with Dynamic with SCROLLDispatchable {

    override def unary_+ : MultiPlayer[T] = this

    override def play[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      require(null != role)
      wrapped match {
        case p: MultiPlayer[_] => addPlaysRelation[T, R](p.wrapped.asInstanceOf[T], role)
        case p: AnyRef => addPlaysRelation[T, R](p.asInstanceOf[T], role)
        case null => throw new RuntimeException(s"Only instances of 'IPlayer' or 'AnyRef' are allowed to play roles!")
      }
      this
    }

    override def <+>[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = play(role)

    override def drop[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = {
      removePlaysRelation[T, R](wrapped, role)
      this
    }

    override def <->[R <: AnyRef : ClassTag](role: R): MultiPlayer[T] = drop(role)

    def applyDynamic[E](name: String)(args: Any*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] = {
      val core = coreFor(wrapped).last
      val roles = plays.roles(core)
      val filtered = dispatchQuery.filter(roles)
      filtered.collect {
        case r if ReflectiveHelper.findMethod(r, name, args).isDefined => (r, ReflectiveHelper.findMethod(r, name, args).get)
      } map { case (r, fm) => dispatch(r, fm, args: _*) } match {
        case Nil => Left(RoleNotFound(core.toString, name, args))
        case l => Right(l)
      }
    }

    def applyDynamicNamed[E](name: String)(args: (String, Any)*)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] =
      applyDynamic(name)(args.map(_._2): _*)(dispatchQuery)

    def selectDynamic[E](name: String)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Either[SCROLLError, Seq[Either[SCROLLError, E]]] = {
      val core = coreFor(wrapped).last
      val roles = plays.roles(core)
      val filtered = dispatchQuery.filter(roles)
      filtered.collect {
        case r if ReflectiveHelper.hasMember(r, name) => r
      } map (ReflectiveHelper.propertyOf(_, name)) match {
        case Nil => Left(RoleNotFound(core.toString, name, Seq.empty))
        case l => Right(l)
      }
    }

    def updateDynamic(name: String)(value: Any)(implicit dispatchQuery: DispatchQuery = DispatchQuery.empty): Unit = {
      val core = coreFor(wrapped).last
      val roles = plays.roles(core)
      val filtered = dispatchQuery.filter(roles)
      filtered.filter(ReflectiveHelper.hasMember(_, name)).foreach(ReflectiveHelper.setPropertyOf(_, name, value))
    }

    override def equals(o: Any): Boolean = o match {
      case other: MultiPlayer[_] => coreFor(wrapped) equals coreFor(other.wrapped)
      case other: Any => coreFor(wrapped) match {
        case Nil => false
        case p :: Nil => p equals other
        case _ => false
      }
      case _ => false // default case
    }

    override def hashCode(): Int = wrapped.hashCode()
  }

}